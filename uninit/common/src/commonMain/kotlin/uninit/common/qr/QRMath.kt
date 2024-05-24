package uninit.common.qr

/**
 * QRMath
 * @author Kazuhiko Arase
 */
internal object QRMath {
    private val EXP_TABLE = IntArray(256)
    private val LOG_TABLE: IntArray

    init {
        for (i in 0..7) {
            EXP_TABLE[i] = 1 shl i
        }

        for (i in 8..255) {
            EXP_TABLE[i] = (EXP_TABLE[i - 4]
                    xor EXP_TABLE[i - 5]
                    xor EXP_TABLE[i - 6]
                    xor EXP_TABLE[i - 8])
        }

        LOG_TABLE = IntArray(256)
        for (i in 0..254) {
            LOG_TABLE[EXP_TABLE[i]] = i
        }
    }

    fun glog(n: Int): Int {
        if (n < 1) {
            throw ArithmeticException("log($n)")
        }

        return LOG_TABLE[n]
    }

    fun gexp(n: Int): Int {
        var n = n
        while (n < 0) {
            n += 255
        }

        while (n >= 256) {
            n -= 255
        }

        return EXP_TABLE[n]
    }
}