package uninit.genesis.discord.client.gateway

import dev.whyoleg.cryptography.BinarySize.Companion.bits
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.asymmetric.RSA
import dev.whyoleg.cryptography.algorithms.digest.SHA256
import dev.whyoleg.cryptography.operations.hash.Hasher
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uninit.common.compose.fytix.ComposableEventBus
import uninit.common.fytix.EventBus
import uninit.common.fytix.None
import uninit.common.fytix.Some
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.auth.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * GatewayQRLoginClient is a client for handling the QR login process.
 * It is meant for the side of the operation that is creating the QR code
 * and authenticating via a WebSocket connection.
 */
class GatewayQRLoginClient(
    engineFactory: HttpClientEngineFactory<*>,
    rsaWidth: Int = 2048,
    private val socketEndpoint: String = "wss://remote-auth-gateway.discord.gg/?v=2",
    private val exchangeEndpoint: String = "https://discord.com/api/v9/users/@me/remote-auth/login",
    private val closeOnToken: Boolean = true,
    private val json: Json = Json { explicitNulls = false; ignoreUnknownKeys = true },
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) : ComposableEventBus("GatewayQRLoginClient") {
    private val http: HttpClient = HttpClient(engineFactory) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(message, null, "GatewayQRAuth")
                }
            }
            level = io.ktor.client.plugins.logging.LogLevel.HEADERS
            filter {
//                parentClient.logLevel >= LogLevel.NETWORK
                true
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        install(ContentNegotiation) {
            json(json)
        }
    }
    private val rsa: RSA.OAEP.KeyPair =
        CryptographyProvider.Default
            .get(RSA.OAEP)
            .keyPairGenerator(
                rsaWidth.bits,
                digest = SHA256
            )
            .generateKeyBlocking()

    private val hasher: Hasher =
        CryptographyProvider.Default
            .get(SHA256)
            .hasher()
    var websocket: DefaultClientWebSocketSession? = null
    var heartbeat: Job = Job()
    init {
        var emitUiLoadingOnClose = true
        once<Any>("token") { emitUiLoadingOnClose = false }
        on<GatewayQRAuthEvent>("ws:close") {
            Napier.d("Gateway QR Login was closed by server.")
            if (emitUiLoadingOnClose) {
                emit("set-loading", GatewayQRAuthUISetLoadingEvent("Getting new login QR..."))
                connect()
            }
        }
    }

    val String.trimRightEquals: String
        get() = this.trimEnd { it == '=' }

    @OptIn(ExperimentalEncodingApi::class)
    fun connect() {
        if (websocket != null && websocket?.isActive == true) return

        scope.launch {
            try {
                http.webSocket(socketEndpoint, request = {
                    header("Origin", "https://discord.com")
                }) {
                    websocket = this
                    while (true) {
                        val other = incoming.receive().let { when (it) {
                            is Frame.Binary -> {
                                Napier.v("Binary frame received: ${it.readBytes().decodeToString()}")
                                None()
                            }
                            is Frame.Close -> {
                                emit("ws:close", GatewayQRAuthEvent("close"))
                                Napier.e("Gateway QR Login was closed by server.")
                                None()
                            }
                            is Frame.Ping -> {
                                send(Frame.Pong(it.readBytes()))
                                Napier.v("Ping received: ${it.readBytes().decodeToString()}")
                                None()
                            }
                            is Frame.Pong -> {
                                Napier.v("Pong received: ${it.readBytes().decodeToString()}")
                                None()
                            }
                            is Frame.Text -> Some(it)
                            else -> {
                                None()
                            }
                        } }
                        if (other is None) continue
                        val othersText = other.unwrap().readText()

                        emit("raw", othersText)
                        try {
                            val packet = json.decodeFromString<GatewayQRAuthEvent>(othersText)
                            emit("ws:${packet.op}", packet)
                            when (packet.op) {
                                "hello" -> {
                                    Napier.v("Hello packet received: $packet")
                                    // setup heartbeat, send encoded public key
                                    if (heartbeat.isActive) {
                                        Napier.w("Cancelling and Overriding old Heartbeat job")
                                        heartbeat.cancel()
                                    }
                                    heartbeat = scope.launch {
                                        while (true) {
                                            delay(packet.heartbeat_interval!!.toLong())
                                            if (isActive) {
                                                Napier.v("Sending heartbeat")
                                                send(
                                                    Frame.Text(
                                                        json.encodeToString(
                                                            GatewayQRAuthEvent.heartbeat()
                                                        )
                                                    )
                                                )
                                            } else {
                                                break
                                            }
                                        }
                                    }
                                    val pubKey = rsa
                                        .publicKey
                                        .encodeTo(RSA.PublicKey.Format.PEM)
                                        .decodeToString()
//                                    Napier.v("Sending public key: $pubKey")

                                    val packetRes = json.encodeToString(
                                        GatewayQRAuthEvent.init(
                                            pubKey
                                                .split('\n').let {
                                                    it.subList(1, it.size - 2).joinToString("")
                                                }
                                        )
                                    )
                                    Napier.v("Sending init packet with public key: $packetRes")
                                    send(Frame.Text(packetRes))
                                }
                                "nonce_proof" -> {
                                    Napier.v("Received nonce proof: ${packet.encrypted_nonce!!}")
                                    val decrypted_nonce = rsa
                                        .privateKey
                                        .decryptor()
                                        .decrypt(
                                            packet.encrypted_nonce!!.decodeBase64Bytes()
                                        )
                                    val proofBytes = hasher.hash(decrypted_nonce)
                                    val proof = json.encodeToString(
                                        GatewayQRAuthEvent.nonceProof(
                                    Base64.UrlSafe.encode(proofBytes).trimRightEquals))
//                                    Napier.v("waiting 500MS")
//                                    delay(500L)
                                    Napier.v("Sending nonce proof: $proof")
                                    send(Frame.Text(proof))
                                }
                                "pending_remote_init" -> {
                                    Napier.v("Received fingerprint: ${packet.fingerprint}")
                                    // emit fingerprint!
                                    emit("fingerprint", GatewayQRAuthFingerprintAvailableEvent(packet.fingerprint!!))
                                }
                                "pending_ticket" -> {
                                    // decrypt and emit user payload
                                    Napier.v("Received pending ticket: ${packet.encrypted_user_payload!!}")
                                    val items = rsa
                                        .privateKey
                                        .decryptor()
                                        .decrypt(
                                            packet.encrypted_user_payload!!.decodeBase64Bytes()
                                        )
                                        .decodeToString()
                                        .split(":")
                                    emit("awaiting-approval", GatewayQRAuthAwaitingApprovalEvent(
                                        userId = items[0],
                                        userAvatarUri = "https://cdn.discordapp.com/avatars/${items[0]}/${items[2]}.png",
                                        userName = items[3]
                                    ))
                                }
                                "pending_login" -> {
                                    // emit loading "Exchanging ticket..."
                                    emit("set-loading", GatewayQRAuthUISetLoadingEvent("Exchanging token..."))
                                    // then exchange ticket, emit token event, and break.
                                    val token = exchangeToken(packet.ticket!!)
                                    emit("token", GatewayQRAuthTokenEvent(token))
                                    break;
                                }
                                "heartbeat_ack" -> {
                                    Napier.v("Heartbeat ack received.")
                                }
                                else -> {
                                    Napier.v("Unknown packet ${packet.op}: $packet")
                                }
                            }
                        } catch (e: Exception) {
                            Napier.e("Error parsing message", e)
                        }
                    }
                    heartbeat.cancel()
                }
            } catch (e: Exception) {

            }
            heartbeat.cancel()
        }
    }

    private suspend fun exchangeToken(ticket: String): String {
        val res = http.post {
            url(exchangeEndpoint)
            header("Origin", "https://discord.com/")
            setBody(QRAuthTicketExchangeRequest(ticket))
            contentType(ContentType.parse("application/json"))
        }
        Napier.v("Exchange response: ${res.status.value}")
        val text = res.bodyAsText()
        Napier.v("Exchange response: $text")
        val encryptedToken = json.decodeFromString<QRAuthTicketExchangeResponse>(text).encrypted_token
        return rsa
            .privateKey
            .decryptor()
            .decrypt(encryptedToken.decodeBase64Bytes())
            .decodeToString()
    }

    fun onAwaitingApproval(block: Event<GatewayQRAuthAwaitingApprovalEvent>.(GatewayQRAuthAwaitingApprovalEvent) -> Unit) =
        on("awaiting-approval", block)

    fun onLoading(block: Event<GatewayQRAuthUISetLoadingEvent>.(GatewayQRAuthUISetLoadingEvent) -> Unit) =
        on("set-loading", block)

    fun onFingerprintAvailable(block: Event<GatewayQRAuthFingerprintAvailableEvent>.(GatewayQRAuthFingerprintAvailableEvent) -> Unit) =
        on("fingerprint", block)

    fun onToken(block: Event<GatewayQRAuthTokenEvent>.(GatewayQRAuthTokenEvent) -> Unit) =
        on("token", block)
}