package uninit.genesis.discord.client.gateway.handlers

import io.github.aakira.napier.Napier
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.GatewayClient

fun initGatewayHandlers(genesisClient: GenesisClient, gateway: GatewayClient) {
    if (genesisClient.logLevel >= LogLevel.VERBOSE) Napier.v("initGatewayHandlers")
    gatewayReadyHandler(genesisClient, gateway)
    gatewayOp1Handler(gateway)
    gatewayOp9Handler(genesisClient, gateway)
    gatewayMessageCreateHandler(genesisClient, gateway)
}