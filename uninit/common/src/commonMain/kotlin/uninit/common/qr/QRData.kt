package uninit.common.qr

/**
 * QRData
 * @author Kazuhiko Arase
 */
abstract class QRData protected constructor(val mode: Int, val data: String) {
    abstract val length: Int

    abstract fun write(buffer: BitBuffer)

    fun getLengthInBits(type: Int): Int {
        return if (type in 1..9) {
            // 1 - 9

            when (mode) {
                Mode.MODE_NUMBER -> 10
                Mode.MODE_ALPHA_NUM -> 9
                Mode.MODE_8BIT_BYTE -> 8
                Mode.MODE_KANJI -> 8
                else -> throw IllegalArgumentException("mode:$mode")
            }
        } else if (type < 27) {
            // 10 - 26

            when (mode) {
                Mode.MODE_NUMBER -> 12
                Mode.MODE_ALPHA_NUM -> 11
                Mode.MODE_8BIT_BYTE -> 16
                Mode.MODE_KANJI -> 10
                else -> throw IllegalArgumentException("mode:$mode")
            }
        } else if (type < 41) {
            // 27 - 40

            when (mode) {
                Mode.MODE_NUMBER -> 14
                Mode.MODE_ALPHA_NUM -> 13
                Mode.MODE_8BIT_BYTE -> 16
                Mode.MODE_KANJI -> 12
                else -> throw IllegalArgumentException("mode:$mode")
            }
        } else {
            throw IllegalArgumentException("type:$type")
        }
    }
}