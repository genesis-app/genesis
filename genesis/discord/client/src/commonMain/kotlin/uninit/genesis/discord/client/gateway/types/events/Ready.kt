package uninit.genesis.discord.client.gateway.types.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uninit.genesis.discord.api.domain.user.UserSettings
import uninit.genesis.discord.entities.guild.ApiGuild

@Serializable
data class Ready(
//    val user: DomainMe, Normal User
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("resume_gateway_url")
    val resumeGatewayUrl: String,
    val shard: List<Int>? = null,
    @SerialName("user_settings")
    val userSettings: UserSettings? = null,
    val guilds: List<ApiGuild>,
)
