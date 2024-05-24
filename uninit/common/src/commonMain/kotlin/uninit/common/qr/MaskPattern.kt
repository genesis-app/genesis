package uninit.common.qr

/**
 * Mask pattern for QR code.
 * @author Kazuhiko Arase, Ported by Aenri Lovehart
 */
internal interface MaskPattern {
    companion object {

        const val PATTERN000: Int = 0

        const val PATTERN001: Int = 1

        const val PATTERN010: Int = 2

        const val PATTERN011: Int = 3

        const val PATTERN100: Int = 4

        const val PATTERN101: Int = 5

        const val PATTERN110: Int = 6

        const val PATTERN111: Int = 7
    }
}