package uninit.genesis.discord.client.gateway.handlers

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.entities.guild.Channel
import uninit.genesis.discord.client.entities.guild.Guild
import uninit.genesis.discord.client.entities.guild.User
import uninit.genesis.discord.client.enum.LogLevel
import uninit.genesis.discord.client.gateway.GatewayClient
import uninit.genesis.discord.client.gateway.types.events.Ready

fun gatewayReadyHandler(genesisClient: GenesisClient, gateway: GatewayClient) {
    val scope = CoroutineScope(Dispatchers.IO)
    gateway.on<Ready>("READY") { ready ->
        if (genesisClient.logLevel >= LogLevel.DEBUG)
            Napier.d("Ready event received", null, "Gateway")
        genesisClient.userSettings = ready.userSettings
        genesisClient.guilds.clear()
        ready.guilds.forEach {
            genesisClient.guilds[it.id.toLong()] = Guild.fromApiGuild(it, genesisClient)
            it.channels?.forEach { channel ->
                genesisClient.channels[channel.id] =
                    Channel.fromApiChannel(channel, it.id.toLong(), genesisClient)
            }
        }

        scope.launch {
            val me = genesisClient.rest.getDomainMe()
            if (me.isOk()) genesisClient.user = me.getOrNull()!!
            else if (genesisClient.logLevel >= LogLevel.ERROR) Napier.e(
                "Error getting user: ${me.errorOrNull()}",
                null,
                "Gateway"
            )
            val user = genesisClient.rest.getUser(me.getOrNull()!!.id)
            if (user.isOk()) {
                val userRes = User.fromApiUser(user.getOrNull()!!, genesisClient)
                genesisClient.normalUser =
                    userRes
                genesisClient.users[userRes.id] = userRes

            } else if (genesisClient.logLevel >= LogLevel.ERROR) Napier.e(
                "Error getting user: ${user.errorOrNull()}",
                null,
                "Gateway"
            )
            val dmsGuild = Guild(
                id = 0L,
                name = "Direct Messages",
                premiumProgressBarEnabled = false,
                genesisClient = genesisClient
            )
            val dms = genesisClient.rest.listDms()
            if (dms.isOk()) {
                val dmChannels = dms.getOrNull()!!
                var position = 0
                dmChannels.forEach { channel ->
                    position++
                    channel.position = position
                    if (channel.name == null) channel.name =
                        channel.recipients!!.joinToString(", ") { it.globalName ?: it.username }
                    genesisClient.channels[channel.id] =
                        Channel.fromApiChannel(channel, 0L, genesisClient)
                }
                genesisClient.guilds[0L] = dmsGuild
            } else if (genesisClient.logLevel >= LogLevel.ERROR) Napier.e(
                "Error getting DMs: ${dms.errorOrNull()}",
                null,
                "Gateway"
            )
            if (genesisClient.logLevel >= LogLevel.DEBUG)
                Napier.d("Gateway Ready", null, "Gateway")
            genesisClient.emit("READY", "")
        }
    }
}