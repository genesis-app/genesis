package uninit.genesis.discord.client.gateway.entities.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceStateUpdate(
    @SerialName("channel_id")
    val channelId: String? = null,
    @SerialName("guild_id")
    val guildId: String? = null,
    @SerialName("self_deaf")
    val deafened: Boolean = false,
    @SerialName("self_mute")
    val muted: Boolean = false,
    @SerialName("self_video")
    val video: Boolean = false,
    val flags: Int = 2
)
