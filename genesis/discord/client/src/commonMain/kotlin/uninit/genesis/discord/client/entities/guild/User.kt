package uninit.genesis.discord.client.entities.guild

import kotlinx.serialization.json.JsonObject
import uninit.genesis.discord.api.domain.user.ApiUser
import uninit.genesis.discord.api.domain.user.PremiumType
import uninit.genesis.discord.api.domain.user.UserFlags
import uninit.genesis.discord.api.types.AssetType
import uninit.genesis.discord.api.types.Snowflake
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.entities.Asset

class User(
    val accentColor: Int? = null,
    val avatar: Asset? = null,

    val avatarDecorationData: JsonObject? = null,
    val banner: Asset? = null,

    val bannerColor: String? = null,
    val bio: String? = null,
    val discriminator: String? = null,
    val email: String? = null,
    val flags: UserFlags? = UserFlags(0),

    val globalName: String? = null,
    val id: Snowflake,

    val mfaEnabled: Boolean = false,

    val nsfwAllowed: Boolean = false,
    val phone: String? = null,

    val premiumType: PremiumType = PremiumType.NONE,
//    
//    val premiumUsageFlags: Int?, // todo: figure out what these are

    val purchasedFlags: Int? = null, // todo: figure out what these are
    val username: String,
    val verified: Boolean = false,

    val genesisClient: GenesisClient
) {

    val displayName: String
        get() = globalName ?: username

    companion object {
        fun fromApiUser(
            apiUser: ApiUser,
            genesisClient: GenesisClient
        ): User = User(
            accentColor = apiUser.accentColor,
            avatar = apiUser.avatar?.let { Asset(genesisClient, it, AssetType.Avatar, apiUser.id) },
            avatarDecorationData = apiUser.avatarDecorationData,
            banner = apiUser.banner?.let { Asset(genesisClient, it, AssetType.Banner, apiUser.id) },
            bannerColor = apiUser.bannerColor,
            bio = apiUser.bio,
            discriminator = apiUser.discriminator,
            email = apiUser.email,
            flags = apiUser.flags,
            globalName = apiUser.globalName,
            id = apiUser.id,
            mfaEnabled = apiUser.mfaEnabled,
            nsfwAllowed = apiUser.nsfwAllowed,
            phone = apiUser.phone,
            premiumType = apiUser.premiumType,
//            premiumUsageFlags = apiUser.premiumUsageFlags,
            purchasedFlags = apiUser.purchasedFlags,
            username = apiUser.username,
            verified = apiUser.verified,
            genesisClient = genesisClient
        )
    }
}