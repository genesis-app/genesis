package uninit.common.qr

/**
 * QRData Mode
 * @author Kazuhiko Arase
 */
interface Mode {
    companion object {

        const val MODE_NUMBER: Int = 1 shl 0

        const val MODE_ALPHA_NUM: Int = 1 shl 1

        const val MODE_8BIT_BYTE: Int = 1 shl 2

        const val MODE_KANJI: Int = 1 shl 3
    }
}