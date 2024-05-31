package uninit.ui.theme

import androidx.compose.ui.graphics.Color
import uninit.common.color.RGBA

fun rgb(r: Int, g: Int, b: Int) = Color(
    r / 255f,
    g / 255f,
    b / 255f,
)

fun Color.atOpacity(opacity: Float) = Color(
    red = red,
    green = green,
    blue = blue,
    alpha = opacity,
)
val Color.asHex: String
    get() = "#${(red * 255f).toInt().toString(16)}${(green * 255f).toInt().toString(16)}${(blue * 255f).toInt().toString(16)}${(alpha * 255f).toInt().toString(16)}"

fun Color.Companion.fromHex(hex: String) = hex.removePrefix("=").let {
    require(it.length in 6..8)
    Color(
        red = it.substring(0, 2).toInt(16) / 255f,
        green = it.substring(2, 4).toInt(16) / 255f,
        blue = it.substring(4, 6).toInt(16) / 255f,
        alpha = if (it.length == 8) it.substring(6, 8).toInt(16) / 255f else 1f,
    )
}
val RGBA.color: Color
    get() = Color(
        red = r / 255f,
        green = g / 255f,
        blue = b / 255f,
        alpha = a / 255f,
    )