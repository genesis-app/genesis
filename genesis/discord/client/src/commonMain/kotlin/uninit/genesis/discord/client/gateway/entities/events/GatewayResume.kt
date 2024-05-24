package uninit.genesis.discord.client.gateway.entities.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GatewayResume(
    var token: String,
    @SerialName("session_id")
    var sessionId: String,
    var seq: Int
)