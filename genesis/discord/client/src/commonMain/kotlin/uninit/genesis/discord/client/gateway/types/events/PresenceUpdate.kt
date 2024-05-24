package uninit.genesis.discord.client.gateway.types.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uninit.genesis.discord.api.domain.user.ClientStatus
import uninit.genesis.discord.api.domain.user.Status

@Serializable
data class PresenceUpdate(
    // TODO: Activities
    // TODO: broadcast
    val client_status: ClientStatus,
    @SerialName("guild_id")
    val guildId: String,
    val status: Status
)