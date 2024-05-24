package uninit.genesis.discord.client.gateway.entities

import kotlinx.serialization.Serializable

@Serializable
data class EmptyGatewayEvent(
    override val op: Int,
    override val s: Int? = null,
    override val t: String? = null,
) : GenericGatewayEvent