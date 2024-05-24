package uninit.common.qr

/**
 * QRNumber
 * @author Kazuhiko Arase
 */
internal class QRNumber(data: String?) : QRData(Mode.MODE_NUMBER, data!!) {
    override fun write(buffer: BitBuffer) {
        val data = data

        var i = 0

        while (i + 2 < data.length) {
            val num = parseInt(data.substring(i, i + 3))
            buffer.put(num, 10)
            i += 3
        }

        if (i < data.length) {
            if (data.length - i == 1) {
                val num = parseInt(data.substring(i, i + 1))
                buffer.put(num, 4)
            } else if (data.length - i == 2) {
                val num = parseInt(data.substring(i, i + 2))
                buffer.put(num, 7)
            }
        }
    }

    override val length: Int
        get() = data.length

    companion object {
        private fun parseInt(s: String): Int {
            var num = 0
            for (element in s) {
                num = num * 10 + parseInt(element)
            }
            return num
        }

        private fun parseInt(c: Char): Int {
            if (c in '0'..'9') {
                return c.code - '0'.code
            }
            throw IllegalArgumentException("illegal char :$c")
        }
    }
}