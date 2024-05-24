package uninit.genesis.discord.client.gateway.entities.events

import kotlinx.serialization.Serializable

@Serializable
data class GatewayHello(
    val heartbeat_interval: Int,
    val _trace: List<String>,
)