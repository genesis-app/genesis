package uninit.genesis.discord.client.gateway.entities

import uninit.genesis.discord.client.gateway.entities.events.GatewayIdentify
import uninit.genesis.discord.client.gateway.entities.events.GatewayResume

object Packet {
    fun resume(token: String, sessionId: String, seq: Int) = GatewayEvent(op = 6, d = GatewayResume(token, sessionId, seq))
    fun identify(token: String) = GatewayEvent(op = 2, d = GatewayIdentify(token))
}