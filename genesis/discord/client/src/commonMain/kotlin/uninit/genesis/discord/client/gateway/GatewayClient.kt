package uninit.genesis.discord.client.gateway

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uninit.common.compose.fytix.ComposableEventBus
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.entities.guild.Channel
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.entities.events.GatewayIdentify
import uninit.genesis.discord.client.gateway.entities.events.GatewayRequestMessages
import uninit.genesis.discord.client.gateway.entities.events.GatewayResume
import uninit.genesis.discord.client.gateway.handlers.initGatewayHandlers
import uninit.genesis.discord.client.gateway.serializers.GatewaySerializer
import uninit.genesis.discord.client.gateway.types.GatewayEvent
import uninit.genesis.discord.client.gateway.types.events.LastMessages
import uninit.genesis.discord.client.gateway.types.events.Ready

class GatewayClient(
    engineFactory: HttpClientEngineFactory<*>,
    val parentClient: GenesisClient,
    val eventBus: ComposableEventBus = parentClient,
) : ComposableEventBus("GatewayClient") {
    private val http = HttpClient(engineFactory) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(message, null, "Gateway")
                }
            }
            level = io.ktor.client.plugins.logging.LogLevel.HEADERS
            filter {
                parentClient.logLevel >= LogLevel.NETWORK
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    val scope = CoroutineScope(Dispatchers.IO)


    var websocket: DefaultClientWebSocketSession? = null


    @PublishedApi
    internal val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    inline fun <reified T> send(data: T) {
        try {
            val text = if (data is String) {
                data
            } else {
                json.encodeToString(data)
            }
            if (websocket?.isActive == true) {
                val frame = Frame.Text(text)
                scope.launch {
                    websocket!!.send(frame)
                }
            } else {
                if (parentClient.logLevel >= LogLevel.ERROR) Napier.e(
                    "Websocket is not active",
                    null,
                    "Gateway"
                )
            }
        } catch (e: Exception) {
            if (parentClient.logLevel >= LogLevel.ERROR) Napier.e(
                "Error sending data",
                e,
                "Gateway"
            )
        }
    }

    fun parseMessage(message: String) = json.decodeFromString(GatewaySerializer, message)

    private var isDisconnecting = false

    var token: String = ""

    fun connect() {
        if (websocket?.isActive == true) return
        var resumePacket: GatewayEvent<GatewayResume>? = null
        var gatewayUrl = "wss://gateway.discord.gg/?v=9&encoding=json"
        var seq: Int? = null
        var retries = 0
        once<Ready>("READY") { ready ->
            resumePacket = GatewayEvent(
                6,
                null,
                null,
                GatewayResume(
                    token = token,
                    sessionId = ready.sessionId,
                    seq = seq!!
                )
            )
            gatewayUrl = ready.resumeGatewayUrl
        }
        scope.launch {
            while (!isDisconnecting) {
                if (retries > 10) {
                    if (parentClient.logLevel >= LogLevel.ERROR) Napier.e(
                        "Gateway failed to connect",
                        null,
                        "Gateway"
                    )
                    break
                }
                try {
                    http.webSocket(gatewayUrl) {
                        websocket = this
                        if (resumePacket === null) {
                            val identifyPacket = GatewayEvent(
                                2, null, null, GatewayIdentify(
                                    token = token,
                                )
                            )
                            send(identifyPacket)
                        } else {
                            retries++
                            if (parentClient.logLevel >= LogLevel.DEBUG)
                                Napier.d("Resuming connection", null, "Gateway")
                            send(resumePacket)
                        }
                        while (true) {
                            val othersMessage = incoming.receive() as? Frame.Text ?: continue
                            val text = othersMessage.readText()
                            emit("raw", text)
                            try {
                                val json = parseMessage(text) as GatewayEvent<*>

                                seq = json.s

                                val event = json.t ?: json.op.toString()

                                if (parentClient.logLevel >= LogLevel.NETWORK)
                                    Napier.d("Received event $event", null, "Gateway")

                                emit(event, json.d)
                            } catch (e: Exception) {
                                val blacklist = listOf(
                                    "Unknown opcode",
                                    "Unknown event",
                                    "Channel was closed"
                                )
                                var isBlacklisted = false
                                for (blacklisted in blacklist) {
                                    if (e.message?.contains(blacklisted) == true) {
                                        isBlacklisted = true
                                        if (parentClient.logLevel >= LogLevel.NETWORK)
                                            Napier.d("Error parsing message", e, "Gateway")
                                        break
                                    }
                                }
                                if (!isBlacklisted)
                                    if (parentClient.logLevel >= LogLevel.ERROR) Napier.e(
                                        "Error parsing message",
                                        e,
                                        "Gateway"
                                    )
                            }
                        }
                    }
                } catch (_: Exception) {
                    retries++
                }
            }
            isDisconnecting = false
            emit("GATEWAY_DISCONNECT", true)
        }
    }

    init {
        initGatewayHandlers(parentClient, this)
    }

    fun disconnect() {
        isDisconnecting = true
        scope.launch {
            websocket?.close()
        }
    }

    suspend fun getLastMessages(channel: Channel) {
        val packet = GatewayEvent(
            34, null, null, GatewayRequestMessages(
                channel.guildId.toString(),
                listOf(channel.id.toString())
            )
        )

        send(packet)

        while (true) {
            val msg = waitFor<LastMessages>("LAST_MESSAGES")
            if (msg.guildId == channel.guildId) {
                channel.addApiMessages(msg.messages)
                break
            }
        }
        return
    }
}