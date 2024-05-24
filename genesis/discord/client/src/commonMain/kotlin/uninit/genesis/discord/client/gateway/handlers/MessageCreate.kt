package uninit.genesis.discord.client.gateway.handlers

import uninit.genesis.discord.api.domain.DomainMessage
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.entities.guild.Message
import uninit.genesis.discord.client.gateway.GatewayClient

fun gatewayMessageCreateHandler(genesisClient: GenesisClient, gateway: GatewayClient) {
    gateway.on<DomainMessage>("MESSAGE_CREATE") { msg ->
        if (!genesisClient.channels.containsKey(msg.channelId)) return@on
        val channel = genesisClient.channels[msg.channelId]!!
        if (!genesisClient.guilds.containsKey(channel.guildId)) return@on
        val message = Message.fromApiMessage(msg, genesisClient)
        channel.messages.add(message)
        channel.sort()
        genesisClient.emit("MESSAGE_CREATE", message)
        channel.emit("MESSAGE_CREATE", message)
    }
}