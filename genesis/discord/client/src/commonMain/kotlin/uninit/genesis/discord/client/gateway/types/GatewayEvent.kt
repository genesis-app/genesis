package uninit.genesis.discord.client.gateway.types

import kotlinx.serialization.Serializable

interface iGatewayEvent {
    val op: Int
    val s: Int?
    val t: String?
}

@Serializable
data class GatewayEvent<T>(
    override val op: Int,
    override val s: Int? = null,
    override val t: String? = null,
    val d: T,
) : iGatewayEvent

@Serializable
data class EmptyGatewayEvent(
    override val op: Int,
    override val s: Int? = null,
    override val t: String? = null,
) : iGatewayEvent

@Serializable
data class BooleanGatewayEvent(
    override val op: Int,
    override val s: Int? = null,
    override val t: String? = null,
    val d: Boolean,
) : iGatewayEvent