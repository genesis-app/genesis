package uninit.genesis.discord.client.entities.guild

import uninit.genesis.discord.api.types.AssetType
import uninit.genesis.discord.api.types.Snowflake
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.entities.Asset
import uninit.genesis.discord.entities.guild.ApiGuild
import uninit.genesis.discord.entities.guild.ApiRole
import uninit.genesis.discord.entities.guild.GuildMember

class Guild(
    val id: Snowflake,
    var name: String,
    var region: String? = null,

    var verificationLevel: Int? = null,

    var defaultMessageNotifications: Int? = null,

    var explicitContentFilter: Int? = null,

    var afkChannelId: String? = null,

    var afkTimeout: Int? = null,
    var icon: Asset? = null,

    var ownerId: String? = null,
    var splash: Asset? = null,

    var discoverySplash: String? = null,
    var banner: Asset? = null,

    var systemChannelId: String? = null,

    var systemChannelFlags: Int? = null,

    var rulesChannelId: String? = null,

    var publicUpdatesChannelId: String? = null,

    var preferredLocale: String? = null,
    var features: List<String>? = null,
    var description: String? = null,

    var premiumProgressBarEnabled: Boolean,

    var safetyAlertsChannelId: String? = null,
    var members: MutableList<GuildMember>? = mutableListOf(),

    val roles: MutableList<ApiRole> = mutableListOf(),

    val genesisClient: GenesisClient
) {

    val channels: List<Channel>
        get() {
            val channels = genesisClient.channels.values.filter { it.guildId == id }.toList()
            return when (id) {
                0L -> channels.sortedBy { it.lastMessageId }
                else -> channels
            }
        }

    companion object {
        fun fromApiGuild(apiGuild: ApiGuild, genesisClient: GenesisClient) = Guild(
            id = if (apiGuild.id === "@me") 0L else apiGuild.id.toLong(),
            name = apiGuild.name,
            region = apiGuild.region,

            verificationLevel = apiGuild.verificationLevel,

            defaultMessageNotifications = apiGuild.defaultMessageNotifications,

            explicitContentFilter = apiGuild.explicitContentFilter,

            afkChannelId = apiGuild.afkChannelId,

            afkTimeout = apiGuild.afkTimeout,
            icon = apiGuild.icon?.let {
                Asset(
                    genesisClient,
                    it,
                    AssetType.Icon,
                    apiGuild.id.toLong()
                )
            },

            ownerId = apiGuild.ownerId,
            splash = apiGuild.splash?.let {
                Asset(
                    genesisClient,
                    it,
                    AssetType.Banner,
                    apiGuild.id.toLong()
                )
            },

            discoverySplash = apiGuild.discoverySplash,
            banner = apiGuild.banner?.let {
                Asset(
                    genesisClient,
                    it,
                    AssetType.Banner,
                    apiGuild.id.toLong()
                )
            },

            systemChannelId = apiGuild.systemChannelId,

            systemChannelFlags = apiGuild.systemChannelFlags,

            rulesChannelId = apiGuild.rulesChannelId,

            publicUpdatesChannelId = apiGuild.publicUpdatesChannelId,

            preferredLocale = apiGuild.preferredLocale,
            features = apiGuild.features,
            description = apiGuild.description,

            premiumProgressBarEnabled = apiGuild.premiumProgressBarEnabled,

            safetyAlertsChannelId = apiGuild.safetyAlertsChannelId,
            roles = apiGuild.roles?.toMutableList() ?: mutableListOf(),
            genesisClient = genesisClient
        )
    }
}