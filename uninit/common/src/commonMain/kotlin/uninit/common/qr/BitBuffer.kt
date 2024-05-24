package uninit.common.qr

/**
 * BitBuffer implementation for QR code.
 * @author Kazuhiko Arase, Ported by Aenri Lovehart
 */
class BitBuffer {
    var buffer: ByteArray
        private set
    var lengthInBits: Int = 0
        private set
    private val inclements = 32

    init {
        buffer = ByteArray(inclements)
    }

    override fun toString(): String {
        val buffer: StringBuilder = StringBuilder()
        for (i in 0 until lengthInBits) {
            buffer.append(if (get(i)) '1' else '0')
        }
        return buffer.toString()
    }

    private fun get(index: Int): Boolean {
        return ((buffer[index / 8].toInt() ushr (7 - index % 8)) and 1) == 1
    }

    fun put(num: Int, length: Int) {
        for (i in 0 until length) {
            put(((num ushr (length - i - 1)) and 1) == 1)
        }
    }

    fun put(bit: Boolean) {
        if (lengthInBits == buffer.size * 8) {
            val newBuffer = ByteArray(buffer.size + inclements)
            buffer.copyInto(newBuffer, buffer.size, 0, buffer.size)
            buffer = newBuffer
        }

        if (bit) {
            buffer[lengthInBits / 8] = (buffer[lengthInBits / 8].toInt() or (0x80 ushr (lengthInBits % 8))).toByte()
        }

        lengthInBits++
    }
}