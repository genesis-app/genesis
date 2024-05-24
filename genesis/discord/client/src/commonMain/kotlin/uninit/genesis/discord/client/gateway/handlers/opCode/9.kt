package uninit.genesis.discord.client.gateway.handlers

import io.github.aakira.napier.Napier
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.GatewayClient

fun gatewayOp9Handler(genesisClient: GenesisClient, gateway: GatewayClient) {
    gateway.on<Boolean>("9") {
        if (it) return@on
        if (genesisClient.logLevel >= LogLevel.ERROR) Napier.e("Gateway session invalidated")
        gateway.disconnect()
        gateway.connect()
    }
}