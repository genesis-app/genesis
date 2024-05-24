package uninit.common.qr


/**
 * ErrorCorrectionLevel for QR code.
 * @author Kazuhiko Arase, Ported by Aenri Lovehart
 */
interface ErrorCorrectionLevel {
    companion object {
        /**
         * 7%.
         */
        const val L: Int = 1

        /**
         * 15%.
         */
        const val M: Int = 0

        /**
         * 25%.
         */
        const val Q: Int = 3

        /**
         * 30%.
         */
        const val H: Int = 2
    }
}