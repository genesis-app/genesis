package uninit.genesis.discord.client.entities.guild

import uninit.genesis.discord.api.domain.ApiMessageEmbed
import uninit.genesis.discord.api.domain.ApiMessageEmbedImage
import uninit.genesis.discord.api.types.Snowflake


class EmbedImage(
    val url: String,
    val proxyUrl: String? = null,
    val height: Int? = null,
    val width: Int? = null,
) {

    val displayUrl: String
        get() = proxyUrl ?: url

    companion object {
        fun fromApiMessageEmbedImage(apiMessageEmbedImage: ApiMessageEmbedImage) = EmbedImage(
            url = apiMessageEmbedImage.url,
            proxyUrl = apiMessageEmbedImage.proxyUrl,
            height = apiMessageEmbedImage.height,
            width = apiMessageEmbedImage.width,
        )
    }
}

enum class EmbedType(val value: Int) {
    IMAGE(1),
    UNKNOWN(-1)
}

class Embed(
    val id: Snowflake,
    val _type: String,
    val image: EmbedImage? = null,
) {

    val type: EmbedType
        get() = when (_type) {
            "image" -> EmbedType.IMAGE
            else -> EmbedType.UNKNOWN
        }

    companion object {
        fun fromApiMessageEmbed(apiMessageEmbed: ApiMessageEmbed) = Embed(
            id = apiMessageEmbed.id,
            _type = apiMessageEmbed.type,
            image = if (apiMessageEmbed.image != null) EmbedImage.fromApiMessageEmbedImage(
                apiMessageEmbed.image!!
            ) else null,
        )
    }
}