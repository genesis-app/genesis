package uninit.genesis.discord.client.entities

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import uninit.genesis.discord.api.types.Asset
import uninit.genesis.discord.api.types.AssetType
import uninit.genesis.discord.api.types.Snowflake
import uninit.genesis.discord.client.GenesisClient

class Asset(
    val genesisClient: GenesisClient,
    val hash: Asset,
    val type: AssetType,
    val parentId: Snowflake? = null,
) {
    private val sizeCache: MutableMap<Int, Any> = mutableMapOf()


    fun toUrl(extension: String = "png", size: Int? = null): String {
        val builder = StringBuilder()
            .append("https://cdn.discordapp.com/")
            .append(type.value)
            .append("/")
        parentId?.let {
            builder.append(it)
                .append("/")
        }
        builder.append(hash)
            .append(".")
            .append(extension)
        size?.let {
            builder.append("?size=")
                .append(it)
        }
        return builder.toString()
    }

//    @Composable
//    fun render(size: Int, extension: String = "png"): Resource<Painter> {
//        if (!sizeCache.keys.contains(size) || !sizeCache[size]!!.isSuccess) {
//            sizeCache[size] = asyncPainterResource(toUrl(extension, size))
//        }
//        return sizeCache[size]!!
//    }
}