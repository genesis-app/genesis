package uninit.genesis.discord.client.gateway.auth

import kotlinx.serialization.Serializable

/**
 * Represents a response to a QR auth ticket exchange request.
 */
@Serializable
data class QRAuthTicketExchangeResponse(
    val encrypted_token: String,
)
