package uninit.common.color

import uninit.common.collections.Four
import uninit.common.collections.four

class RGBA(
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int = 255,
){
    init {
        require(r in 0..255)
        require(g in 0..255)
        require(b in 0..255)
        require(a in 0..255)
    }

    override fun toString(): String = "RGBA($r, $g, $b, $a)"
    fun toFloat(): Four<Float> =
        arrayOf(
            r / 255f,
            g / 255f,
            b / 255f,
            a / 255f
        ).four
    companion object {
        fun hex(it: String): RGBA {
            require(it.length in 6..9)
            it.removePrefix("#").let {
                val r = it.substring(0, 2).toInt(16)
                val g = it.substring(2, 4).toInt(16)
                val b = it.substring(4, 6).toInt(16)
                val a = if (it.length == 8) it.substring(6, 8).toInt(16) else 255
                return RGBA(r, g, b, a)
            }
        }
    }
}