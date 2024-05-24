package uninit.genesis.discord.client.gateway

import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import uninit.common.fytix.Err
import uninit.common.fytix.Ok
import uninit.common.fytix.result
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.entities.EmptyGatewayEvent
import uninit.genesis.discord.client.gateway.entities.GatewayEvent
import uninit.genesis.discord.client.gateway.entities.GenericGatewayEvent
import uninit.genesis.discord.client.gateway.entities.Packet
import uninit.genesis.discord.client.gateway.entities.events.GatewayResume

class Gateway(
    val parent: GenesisClient,
    engineFactory: HttpClientEngineFactory<*>,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    var gatewayUrl: String = "wss://gateway.discord.gg/?v=9&encoding=json",
    var token: String = "",
) {
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
                parent.logLevel >= LogLevel.NETWORK
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }
    var socket: DefaultClientWebSocketSession? = null
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    internal var resumePacket: GatewayEvent<GatewayResume>? = null
    internal var seq: Int? = null
    private var _socketJob: Job? = null
    internal var socketJob: Job
        get() = _socketJob!!
        set(value) {
            if (_socketJob != null) _socketJob!!.cancel()
            _socketJob = value
        }

    suspend inline fun <E : GenericGatewayEvent> push(event: E) {
        socket?.send(Frame.Text(json.encodeToString(GatewaySerializer, event)))
    }

    inline fun <E : GenericGatewayEvent> enqueuePush(event: E) {
        scope.launch {
            push(event)
        }
    }

    fun openGatewayConnection() = scope.launch {
        if (socket != null) {
            Napier.w("Socket is already open, closing socket", null, "GATEWAY")
            socket?.close()
        }
        var retries = 0
        http.webSocket(gatewayUrl) {
            socket = this
            Napier.v("Connected", null, "GATEWAY")
            parent.onceReady {
                with(it) {
                    resumePacket = Packet.resume(token, sessionId, seq!!)
                    gatewayUrl = resumeGatewayUrl
                }
            }
            when (resumePacket) {
                null -> {
                    enqueuePush(Packet.identify(token))
                }
                else -> {
                    retries++
                    Napier.w("Resuming connection, retries=$retries", null, "GATEWAY")
                    enqueuePush(resumePacket!!)
                }
            }
            while (true) {
                when (val frame = incoming.receive()) {
                    is Frame.Text -> {
                        processTextPacket(frame.readText())
                    }
                    else -> {
                        Napier.v("Received frame: $frame", null, "GATEWAY")
                    }
                }
            }
        }
    }.also { socketJob = it }

    internal suspend fun processTextPacket(text: String) {
        Napier.d("Received new text frame, processing possible gateway event.", null, "GATEWAY")
        when (val gatewayEvent = result { json.decodeFromString(GatewaySerializer, text) }) {
            is Ok -> {
                val packet = gatewayEvent.value
                Napier.v("Received gateway event {s=${packet.s},t=${packet.t}}")
                seq = packet.s
                when (packet) {
                    is GatewayEvent<*> -> {
                        parent.emit(packet.t ?: "op${packet.op}", packet.d)
                    }
                    is EmptyGatewayEvent -> {
                        parent.emit(packet.t ?: "op${packet.op}", null)
                    }
                }
            }
            is Err -> {
                Napier.e("Failed to decode gateway event, handling...", gatewayEvent.error, "GATEWAY")
                if (
                    listOf(
                        "Unknown opcode",
                        "Unknown event",
                        "Channel was closed"
                    ).map { gatewayEvent.error.message?.contains(it) }.contains(true)
                ) {
                    Napier.e("Gateway connection was closed and blacklisted, cancelling socket job and closing socket", null, "GATEWAY")
                    parent.emit("GENESIS_GATEWAY_CLOSED", gatewayEvent.error)
                    socket?.closeExceptionally(gatewayEvent.error)
                    socketJob.cancel()
                } else {
                    Napier.e("Failed to decode: \n$text", null, "GATEWAY")
                    parent.emit("GENESIS_GATEWAY_DECODE_ERR", gatewayEvent.error to text)

                }
            }
        }
    }
}