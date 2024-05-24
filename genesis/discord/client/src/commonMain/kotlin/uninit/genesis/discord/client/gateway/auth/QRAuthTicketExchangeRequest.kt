package uninit.genesis.discord.client.gateway.auth

import kotlinx.serialization.Serializable

/**
 * Represents a request to exchange a QR auth ticket for a token.
 */
@Serializable
data class QRAuthTicketExchangeRequest(
    val ticket: String
)
