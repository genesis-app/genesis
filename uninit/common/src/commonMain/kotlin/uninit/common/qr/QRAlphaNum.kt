package uninit.common.qr


/**
 * QRAlphaNum
 * @author Kazuhiko Arase
 */
internal class QRAlphaNum(data: String?) : QRData(Mode.MODE_ALPHA_NUM, data!!) {
    override fun write(buffer: BitBuffer) {
        val c = data.toCharArray()

        var i = 0

        while (i + 1 < c.size) {
            buffer.put(getCode(c[i]) * 45 + getCode(c[i + 1]), 11)
            i += 2
        }

        if (i < c.size) {
            buffer.put(getCode(c[i]), 6)
        }
    }

    override val length: Int
        get() = data.length

    companion object {
        private fun getCode(c: Char): Int = when (c) {
            in '0'..'9' -> c.code - '0'.code
            in 'A'..'Z' -> c.code - 'A'.code + 10
            ' ' -> 36
            '$' -> 37
            '%' -> 38
            '*' -> 39
            '+' -> 40
            '-' -> 41
            '.' -> 42
            '/' -> 43
            ':' -> 44
            else -> throw IllegalArgumentException("illegal char :$c")
        }
    }
}