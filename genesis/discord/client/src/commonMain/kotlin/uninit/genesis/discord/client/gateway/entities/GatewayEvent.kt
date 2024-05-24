package uninit.genesis.discord.client.gateway.entities

import kotlinx.serialization.Serializable

@Serializable
class GatewayEvent<T>(
    override val op: Int,
    override val s: Int? = null,
    override val t: String? = null,
    val d: T? = null,
) : GenericGatewayEvent
