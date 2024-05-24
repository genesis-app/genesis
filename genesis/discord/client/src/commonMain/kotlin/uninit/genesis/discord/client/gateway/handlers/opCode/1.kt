package uninit.genesis.discord.client.gateway.handlers

import uninit.genesis.discord.client.gateway.GatewayClient
import uninit.genesis.discord.client.gateway.types.EmptyGatewayEvent
import uninit.genesis.discord.client.gateway.types.events.Ready

fun gatewayOp1Handler(gateway: GatewayClient) {
    gateway.on<Ready>("1") {
        gateway.send(EmptyGatewayEvent(11))
    }
}