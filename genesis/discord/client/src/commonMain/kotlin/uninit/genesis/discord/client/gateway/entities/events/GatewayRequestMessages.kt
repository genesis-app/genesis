package uninit.genesis.discord.client.gateway.entities.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GatewayRequestMessages(
    @SerialName("guild_id")
    val guildId: String,
    @SerialName("channel_ids")
    val channelIds: List<String>,
)