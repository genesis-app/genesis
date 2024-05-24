package uninit.genesis.discord.client

import androidx.compose.runtime.mutableStateMapOf
import io.ktor.client.engine.*
import uninit.common.compose.fytix.ComposableEventBus
import uninit.common.fytix.Err
import uninit.common.fytix.Ok
import uninit.common.fytix.Result
import uninit.genesis.discord.api.ApiError
import uninit.genesis.discord.api.annotations.ExperimentalDiscordApi
import uninit.genesis.discord.api.domain.DomainMessage
import uninit.genesis.discord.api.domain.user.DomainMe
import uninit.genesis.discord.api.domain.user.UserSettings
import uninit.genesis.discord.api.types.Snowflake
import uninit.genesis.discord.client.entities.guild.Channel
import uninit.genesis.discord.client.entities.guild.Guild
import uninit.genesis.discord.client.entities.guild.User
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.GatewayClient
import uninit.genesis.discord.client.gateway.types.events.Ready
import uninit.genesis.discord.client.rest.RestClient
import uninit.genesis.discord.entities.guild.GuildMember

@OptIn(ExperimentalDiscordApi::class)
class GenesisClient(
    httpClientEngineFactory: HttpClientEngineFactory<*>,
) : ComposableEventBus("GenesisClient") {
    lateinit var user: DomainMe // ClientUser
    lateinit var normalUser: User
    internal var loggedIn: Boolean = false

    var logLevel: LogLevel = LogLevel.INFO
    val rest: RestClient = RestClient(httpClientEngineFactory, genesisClient = this)
    val gateway: GatewayClient = GatewayClient(httpClientEngineFactory, parentClient = this)

    var guilds = mutableStateMapOf<Snowflake, Guild?>()
    var channels = mutableStateMapOf<Snowflake, Channel>()
    var users = mutableStateMapOf<Snowflake, User>()
    var guildMembers = mutableStateMapOf<Snowflake, GuildMember>()
    var userSettings: UserSettings? = null

    suspend fun tryTokenLogin(token: String): Result<DomainMe, ApiError> {
        rest.token = token
        return when (val response = rest.getDomainMe()) {
            is Ok -> {
                user = response.value
                loggedIn = true
                Ok(response.value)
            }

            is Err -> {
                rest.token = "" // Clear token
                Err(response.error)
            }
        }

    }

    /**
     * Explicit typing for [on] function, event is [Ready]
     */
    fun onReady(block: Event<Ready>.(Ready) -> Unit) = on("READY", block)

    /**
     * Explicit typing for [once] function, event is [Ready]
     */
    fun onceReady(block: Event<Ready>.(Ready) -> Unit) = once("READY", block)

    /**
     * Explicit typing for [waitFor] function, event is [Ready]
     */
    suspend fun waitForReady() = waitFor<Ready>("READY")

    /**
     * Explicit typing for [on] function, event is [DomainMessage]
     */
    fun onMessageCreate(block: Event<DomainMessage>.(DomainMessage) -> Unit) =
        on("MESSAGE_CREATE", block)

    /**
     * Explicit typing for [once] function, event is [DomainMessage]
     */
    fun onceMessageCreate(block: Event<DomainMessage>.(DomainMessage) -> Unit) =
        once("MESSAGE_CREATE", block)

    init {
        onMessageCreate {

        }
    }

}