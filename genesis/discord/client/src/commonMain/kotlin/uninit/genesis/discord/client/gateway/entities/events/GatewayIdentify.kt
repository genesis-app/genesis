package uninit.genesis.discord.client.gateway.entities.events

import kotlinx.serialization.Serializable
import uninit.genesis.discord.client.gateway.types.SuperProperties
import uninit.genesis.discord.client.getSuperProperties

/**
 * GatewayIdentify is the payload sent to the gateway to identify the client.
 *
 * @opCode 2
 *
 * @param token The authentication token.
 * @param properties The properties of the client.
 * @param compress Whether this connection supports compression of packets.
 *
 */
@Serializable
data class GatewayIdentify(
    val token: String,
    val properties: SuperProperties = getSuperProperties(),
    val compress: Boolean = false,
    // TODO: PRESENCE UPDATE
) {
}