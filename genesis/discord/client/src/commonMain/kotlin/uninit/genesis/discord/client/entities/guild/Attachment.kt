package uninit.genesis.discord.client.entities.guild

import uninit.genesis.discord.api.types.Snowflake

enum class AttachmentType(val value: Int) {
    IMAGE(0),
    UNKNOWN(-1),
}

class Attachment(
    var contentType: String,
    val filename: String,
    val height: Int? = null,
    val id: Snowflake,
    val proxyUrl: String,
    val url: String,
    val width: Int? = null,
) {

    val type: AttachmentType
        get() = when (contentType) {
            "image/jpeg", "image/png", "image/webp", "image/gif" -> AttachmentType.IMAGE
            else -> AttachmentType.UNKNOWN
        }

    companion object {
        fun fromApiMessageAttachment(apiMessageAttachment: uninit.genesis.discord.api.domain.ApiMessageAttachment) =
            Attachment(
                contentType = apiMessageAttachment.contentType,
                filename = apiMessageAttachment.filename,
                height = apiMessageAttachment.height,
                id = apiMessageAttachment.id,
                proxyUrl = apiMessageAttachment.proxyUrl,
                url = apiMessageAttachment.url,
                width = apiMessageAttachment.width,
            )
    }
}