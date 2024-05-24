package uninit.genesis.discord.client.rest

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import uninit.common.fytix.Err
import uninit.common.fytix.Ok
import uninit.common.fytix.Result
import uninit.genesis.discord.api.ApiError
import uninit.genesis.discord.api.domain.DomainMessage
import uninit.genesis.discord.api.domain.user.ApiUser
import uninit.genesis.discord.api.domain.user.DomainMe
import uninit.genesis.discord.api.domain.user.DomainUserProfile
import uninit.genesis.discord.api.domain.user.UserSettings
import uninit.genesis.discord.api.types.Snowflake
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.types.SuperProperties
import uninit.genesis.discord.client.getSuperProperties
import uninit.genesis.discord.entities.guild.ApiChannel
import uninit.genesis.discord.entities.guild.ApiGuild
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import io.ktor.client.plugins.logging.LogLevel as KtorLogLevel

class RestClient(
    engineFactory: HttpClientEngineFactory<*>,
    baseUrl: String = "https://discord.com/api/v9/",
    genesisClient: GenesisClient
) {
    val http = HttpClient(engineFactory) {
        defaultRequest {
            url(baseUrl)
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(message, null, "Rest")
                }
            }
            level = KtorLogLevel.HEADERS
            filter {
                genesisClient.logLevel >= LogLevel.NETWORK
            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    var token: String? = null

    internal suspend inline fun <reified T, reified E> get(endpoint: String): Result<T, E> {
        val res = http.get(endpoint) {
            headers {
                if (token !== null) header("Authorization", token)
            }
        }
        return when (res.status) {
            HttpStatusCode.OK -> {
                Ok(res.body())
            }

            else -> {
                Err(res.body())
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend inline fun <reified I, reified T, reified E> post(
        endpoint: String,
        body: I
    ): Result<T, E> {
        val res = http.post(endpoint) {
            setBody(body)
            headers {
                if (token !== null) header("Authorization", token)
                header("Content-Type", "application/json")

                val superProperties =
                    Json.encodeToString(SuperProperties.serializer(), getSuperProperties())
                header("X-Super-Properties", Base64.encode(superProperties.toByteArray()))
            }
        }
        return when (res.status) {
            HttpStatusCode.OK -> {
                Ok(res.body())
            }

            else -> {
                Err(res.body())
            }
        }
    }

    suspend fun getDomainMe(): Result<DomainMe, ApiError> = get("users/@me")
    suspend fun getUser(userId: Snowflake): Result<ApiUser, ApiError> = get("users/$userId")
    suspend fun getDomainUser(
        userId: Snowflake,
        withMutualGuilds: Boolean = true
    ): Result<DomainUserProfile, ApiError> =
        get("users/$userId/profile?with_mutual_guilds=$withMutualGuilds&with_mutual_friends_count=false")

    suspend fun getGuild(guildId: Snowflake): Result<ApiGuild, ApiError> = get("guilds/$guildId")
    suspend fun listDms(): Result<List<ApiChannel>, ApiError> = get("users/@me/channels")
    suspend fun getUserSettings(): Result<UserSettings, ApiError> = get("users/@me/settings")

    suspend fun getMessages(
        channelId: Snowflake,
        limit: Int = 50,
        after: Snowflake? = null
    ): Result<List<DomainMessage>, ApiError> =
        get("channels/$channelId/messages?limit=$limit${if (after !== null) "&after=$after" else ""}")

    suspend fun sendMessage(
        channelId: Snowflake,
        message: DomainMessage
    ): Result<DomainMessage, ApiError> =
        post("channels/$channelId/messages", message)
}