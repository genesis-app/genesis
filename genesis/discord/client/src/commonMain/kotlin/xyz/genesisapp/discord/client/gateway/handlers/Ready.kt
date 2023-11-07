package xyz.genesisapp.discord.client.gateway.handlers

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import xyz.genesisapp.discord.client.GenesisClient
import xyz.genesisapp.discord.client.entities.guild.Channel
import xyz.genesisapp.discord.client.entities.guild.Guild
import xyz.genesisapp.discord.client.gateway.GatewayClient
import xyz.genesisapp.discord.client.gateway.types.events.Ready
import xyz.genesisapp.discord.entities.guild.ApiGuild

fun gatewayReadyHandler(genesisClient: GenesisClient, gateway: GatewayClient) {
    val scope = CoroutineScope(Dispatchers.IO)
    gateway.on<Ready>("READY") { ready ->
        Napier.d("Ready event received", null, "Gateway")
        gateway.sessionId = ready.sessionId
        genesisClient.userSettings = ready.userSettings
        val guilds = ready.guilds.map<ApiGuild, Guild> { Guild.fromApiGuild(it, genesisClient) }
        genesisClient.guilds = guilds.associateBy { it.id }.toMutableMap()

        ready.guilds.forEach {
            it.channels?.forEach { channel ->
                genesisClient.channels[channel.id] =
                    Channel.fromApiChannel(channel, it.id, genesisClient)
            }
        }

        scope.launch {
            val me = genesisClient.rest.getDomainMe()
            if (me.isOk()) genesisClient.user = me.getOrNull()!!
            else Napier.e("Error getting user: ${me.errorOrNull()}", null, "Gateway")
            Napier.d("Gateway Ready", null, "Gateway")
            genesisClient.events.emit("READY", "")
        }
    }
}