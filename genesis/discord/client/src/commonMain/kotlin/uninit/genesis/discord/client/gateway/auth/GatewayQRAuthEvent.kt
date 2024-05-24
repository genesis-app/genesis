package uninit.genesis.discord.client.gateway.auth

import kotlinx.serialization.Serializable

/**
 * This event is used to communicate with the QR Auth system.
 */
@Serializable
data class GatewayQRAuthEvent(
    /**
     * The operation this event describes.
     * Possible values are:
     * - heartbeat
     * - heartbeat_ack
     * - hello
     * - init
     * - nonce_proof
     * - pending_remote_init
     * - pending_ticket
     * - pending_login
     * - close
     */
    val op: String,

    /**
     * This value is only sent, and only with the `init` operation.
     */
    val encoded_public_key: String? = null,
    /**
     * This value is only received, and only with the `nonce_proof` operation.
     */
    val encrypted_nonce: String? = null,
    /**
     * This value is only received, and only with the `pending_ticket` operation.
     */
    val encrypted_user_payload: String? = null,

    /**
     * This value is only sent, and only with the `nonce_proof` operation.
     */
    val proof: String? = null,
    /**
     * This value is only received, and only with the `pending_login` operation.
     */
    val ticket: String? = null,
    /**
     * This value is only received, and only with the `hello` operation.
     */
    val heartbeat_interval: Int? = null,
    /**
     * This value is only received, and only with the `pending_remote_init` operation.
     */
    val fingerprint: String?  = null,
) {
    companion object {
        fun nonceProof(proof: String): GatewayQRAuthEvent {
            return GatewayQRAuthEvent("nonce_proof", proof = proof)
        }
        fun init(encodedPublicKey: String): GatewayQRAuthEvent {
            return GatewayQRAuthEvent("init", encoded_public_key = encodedPublicKey)
        }
        fun heartbeat(): GatewayQRAuthEvent {
            return GatewayQRAuthEvent("heartbeat")
        }
    }
}