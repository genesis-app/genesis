package uninit.common



class LinearGradient(vararg colors: GradientStop) {
    val colors: List<GradientStop>
    init {
        if (colors.filter { it.stop != null }.map { it.stop }.distinct().size != colors.filter { it.stop != null }.size) {
            throw IllegalArgumentException("Stops must be unique")
        } else if (
            colors.filter { it.stop != null }.map { it.stop }.any { it!! > 100 || it < 0 }
            || colors.filter { it.stop != null }.let { it.isNotEmpty() && it.size != colors.size }
        ) {
            throw IllegalArgumentException("Stops must be between 0 and 100 and must be specified for all colors or none")
        }
        this.colors = colors.toList()

    }

    data class GradientStop(val rgba: RGBA, val stop: Float?)
    data class RGBA(val r: Int, val g: Int, val b: Int, val a: Int) {
        companion object {
            val BLACK: RGBA
                get() = RGBA(0, 0, 0, 255)
            val WHITE: RGBA
                get() = RGBA(255, 255, 255, 255)
        }
    }
    


    operator fun get(index: Float): RGBA {
        if (index < 0 || index > 100) {
            throw IllegalArgumentException("Index must be between 0 and 100")
        }
        val stops = colors.filter { it.stop != null }
        if (stops.filter {
            if (it.stop == index) {
                return@get it.rgba
            } else {
                return@filter false
            }}.let { true } /* micro-optimization here, micro-optimization there */) {

            val lastColor = stops.filter { it.stop!! < index }.maxByOrNull { it.stop!! }
            val nextColor = stops.filter { it.stop!! > index }.minByOrNull { it.stop!! }

            if (lastColor == null) {
                return nextColor!!.rgba
            } else if (nextColor == null) {
                return lastColor.rgba
            } else {
                val lastColorIndex = lastColor.stop!!
                val nextColorIndex = nextColor.stop!!
                val lastColorRGBA = lastColor.rgba
                val nextColorRGBA = nextColor.rgba
                val percent = (index - lastColorIndex) / (nextColorIndex - lastColorIndex)
                return RGBA(
                    (lastColorRGBA.r + (nextColorRGBA.r - lastColorRGBA.r) * percent).toInt(),
                    (lastColorRGBA.g + (nextColorRGBA.g - lastColorRGBA.g) * percent).toInt(),
                    (lastColorRGBA.b + (nextColorRGBA.b - lastColorRGBA.b) * percent).toInt(),
                    (lastColorRGBA.a + (nextColorRGBA.a - lastColorRGBA.a) * percent).toInt()
                )
            }

        }
        throw IllegalStateException("Unreachable.")
    }
}

fun color(r: Int, g: Int, b: Int, a: Int = 255, stopPercent: Float? = null): LinearGradient.GradientStop {
    return LinearGradient.GradientStop(LinearGradient.RGBA(r, g, b, a), stopPercent)
}
fun color(color: LinearGradient.RGBA, stopPercent: Float? = null): LinearGradient.GradientStop {
    return LinearGradient.GradientStop(color, stopPercent)
}

fun linearGradient(vararg colors: LinearGradient.GradientStop): LinearGradient {
    return LinearGradient(*colors)
}