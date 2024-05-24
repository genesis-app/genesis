
import uninit.common.qr.*
import uninit.common.qr.MaskPattern
import uninit.common.qr.Polynomial
import kotlin.math.abs

/**
 * QRUtil
 * @author Kazuhiko Arase
 */
internal object QRUtil {
    val jISEncoding: String
        get() = "SJIS"

    fun getPatternPosition(typeNumber: Int): IntArray {
        return PATTERN_POSITION_TABLE[typeNumber - 1]
    }

    private val PATTERN_POSITION_TABLE = arrayOf(
        intArrayOf(),
        intArrayOf(6, 18),
        intArrayOf(6, 22),
        intArrayOf(6, 26),
        intArrayOf(6, 30),
        intArrayOf(6, 34),
        intArrayOf(6, 22, 38),
        intArrayOf(6, 24, 42),
        intArrayOf(6, 26, 46),
        intArrayOf(6, 28, 50),
        intArrayOf(6, 30, 54),
        intArrayOf(6, 32, 58),
        intArrayOf(6, 34, 62),
        intArrayOf(6, 26, 46, 66),
        intArrayOf(6, 26, 48, 70),
        intArrayOf(6, 26, 50, 74),
        intArrayOf(6, 30, 54, 78),
        intArrayOf(6, 30, 56, 82),
        intArrayOf(6, 30, 58, 86),
        intArrayOf(6, 34, 62, 90),
        intArrayOf(6, 28, 50, 72, 94),
        intArrayOf(6, 26, 50, 74, 98),
        intArrayOf(6, 30, 54, 78, 102),
        intArrayOf(6, 28, 54, 80, 106),
        intArrayOf(6, 32, 58, 84, 110),
        intArrayOf(6, 30, 58, 86, 114),
        intArrayOf(6, 34, 62, 90, 118),
        intArrayOf(6, 26, 50, 74, 98, 122),
        intArrayOf(6, 30, 54, 78, 102, 126),
        intArrayOf(6, 26, 52, 78, 104, 130),
        intArrayOf(6, 30, 56, 82, 108, 134),
        intArrayOf(6, 34, 60, 86, 112, 138),
        intArrayOf(6, 30, 58, 86, 114, 142),
        intArrayOf(6, 34, 62, 90, 118, 146),
        intArrayOf(6, 30, 54, 78, 102, 126, 150),
        intArrayOf(6, 24, 50, 76, 102, 128, 154),
        intArrayOf(6, 28, 54, 80, 106, 132, 158),
        intArrayOf(6, 32, 58, 84, 110, 136, 162),
        intArrayOf(6, 26, 54, 82, 110, 138, 166),
        intArrayOf(6, 30, 58, 86, 114, 142, 170)
    )

    private val MAX_LENGTH = arrayOf(
        arrayOf(
            intArrayOf(41, 25, 17, 10),
            intArrayOf(34, 20, 14, 8),
            intArrayOf(27, 16, 11, 7),
            intArrayOf(17, 10, 7, 4)
        ),
        arrayOf(
            intArrayOf(77, 47, 32, 20),
            intArrayOf(63, 38, 26, 16),
            intArrayOf(48, 29, 20, 12),
            intArrayOf(34, 20, 14, 8)
        ),
        arrayOf(
            intArrayOf(127, 77, 53, 32),
            intArrayOf(101, 61, 42, 26),
            intArrayOf(77, 47, 32, 20),
            intArrayOf(58, 35, 24, 15)
        ),
        arrayOf(
            intArrayOf(187, 114, 78, 48),
            intArrayOf(149, 90, 62, 38),
            intArrayOf(111, 67, 46, 28),
            intArrayOf(82, 50, 34, 21)
        ),
        arrayOf(
            intArrayOf(255, 154, 106, 65),
            intArrayOf(202, 122, 84, 52),
            intArrayOf(144, 87, 60, 37),
            intArrayOf(106, 64, 44, 27)
        ),
        arrayOf(
            intArrayOf(322, 195, 134, 82),
            intArrayOf(255, 154, 106, 65),
            intArrayOf(178, 108, 74, 45),
            intArrayOf(139, 84, 58, 36)
        ),
        arrayOf(
            intArrayOf(370, 224, 154, 95),
            intArrayOf(293, 178, 122, 75),
            intArrayOf(207, 125, 86, 53),
            intArrayOf(154, 93, 64, 39)
        ),
        arrayOf(
            intArrayOf(461, 279, 192, 118),
            intArrayOf(365, 221, 152, 93),
            intArrayOf(259, 157, 108, 66),
            intArrayOf(202, 122, 84, 52)
        ),
        arrayOf(
            intArrayOf(552, 335, 230, 141),
            intArrayOf(432, 262, 180, 111),
            intArrayOf(312, 189, 130, 80),
            intArrayOf(235, 143, 98, 60)
        ),
        arrayOf(
            intArrayOf(652, 395, 271, 167),
            intArrayOf(513, 311, 213, 131),
            intArrayOf(364, 221, 151, 93),
            intArrayOf(288, 174, 119, 74)
        )
    )

    fun getMaxLength(typeNumber: Int, mode: Int, errorCorrectionLevel: Int): Int {
        val t = typeNumber - 1
        var e = 0
        var m = 0

        e = when (errorCorrectionLevel) {
            ErrorCorrectionLevel.L -> 0
            ErrorCorrectionLevel.M -> 1
            ErrorCorrectionLevel.Q -> 2
            ErrorCorrectionLevel.H -> 3
            else -> throw IllegalArgumentException("e:$errorCorrectionLevel")
        }
        m = when (mode) {
            Mode.MODE_NUMBER -> 0
            Mode.MODE_ALPHA_NUM -> 1
            Mode.MODE_8BIT_BYTE -> 2
            Mode.MODE_KANJI -> 3
            else -> throw IllegalArgumentException("m:$mode")
        }
        return MAX_LENGTH[t][e][m]
    }


    /**
     * エラー訂正多項式を取得する。
     */
    fun getErrorCorrectPolynomial(errorCorrectLength: Int): Polynomial {
        var a: Polynomial = Polynomial(intArrayOf(1))

        for (i in 0 until errorCorrectLength) {
            a = a.multiply(Polynomial(intArrayOf(1, QRMath.gexp(i))))
        }

        return a
    }

    /**
     * 指定されたパターンのマスクを取得する。
     */
    fun getMask(maskPattern: Int, i: Int, j: Int): Boolean {
        return when (maskPattern) {
            MaskPattern.PATTERN000 -> (i + j) % 2 == 0
            MaskPattern.PATTERN001 -> i % 2 == 0
            MaskPattern.PATTERN010 -> j % 3 == 0
            MaskPattern.PATTERN011 -> (i + j) % 3 == 0
            MaskPattern.PATTERN100 -> (i / 2 + j / 3) % 2 == 0
            MaskPattern.PATTERN101 -> i * j % 2 + (i * j) % 3 == 0
            MaskPattern.PATTERN110 -> ((i * j) % 2 + (i * j) % 3) % 2 == 0
            MaskPattern.PATTERN111 -> ((i * j) % 3 + (i + j) % 2) % 2 == 0

            else -> throw IllegalArgumentException("mask:$maskPattern")
        }
    }

    /**
     * 失点を取得する
     */
    fun getLostPoint(qrCode: QRCode): Int {
        val moduleCount = qrCode.moduleCount

        var lostPoint = 0


        // LEVEL1
        for (row in 0 until moduleCount) {
            for (col in 0 until moduleCount) {
                var sameCount = 0
                val dark = qrCode.isDark(row, col)

                for (r in -1..1) {
                    if (row + r < 0 || moduleCount <= row + r) {
                        continue
                    }

                    for (c in -1..1) {
                        if (col + c < 0 || moduleCount <= col + c) {
                            continue
                        }

                        if (r == 0 && c == 0) {
                            continue
                        }

                        if (dark == qrCode.isDark(row + r, col + c)) {
                            sameCount++
                        }
                    }
                }

                if (sameCount > 5) {
                    lostPoint += (3 + sameCount - 5)
                }
            }
        }

        // LEVEL2
        for (row in 0 until moduleCount - 1) {
            for (col in 0 until moduleCount - 1) {
                var count = 0
                if (qrCode.isDark(row, col)) count++
                if (qrCode.isDark(row + 1, col)) count++
                if (qrCode.isDark(row, col + 1)) count++
                if (qrCode.isDark(row + 1, col + 1)) count++
                if (count == 0 || count == 4) {
                    lostPoint += 3
                }
            }
        }

        // LEVEL3
        for (row in 0 until moduleCount) {
            for (col in 0 until moduleCount - 6) {
                if (qrCode.isDark(row, col)
                    && !qrCode.isDark(row, col + 1)
                    && qrCode.isDark(row, col + 2)
                    && qrCode.isDark(row, col + 3)
                    && qrCode.isDark(row, col + 4)
                    && !qrCode.isDark(row, col + 5)
                    && qrCode.isDark(row, col + 6)
                ) {
                    lostPoint += 40
                }
            }
        }

        for (col in 0 until moduleCount) {
            for (row in 0 until moduleCount - 6) {
                if (qrCode.isDark(row, col)
                    && !qrCode.isDark(row + 1, col)
                    && qrCode.isDark(row + 2, col)
                    && qrCode.isDark(row + 3, col)
                    && qrCode.isDark(row + 4, col)
                    && !qrCode.isDark(row + 5, col)
                    && qrCode.isDark(row + 6, col)
                ) {
                    lostPoint += 40
                }
            }
        }

        // LEVEL4
        var darkCount = 0

        for (col in 0 until moduleCount) {
            for (row in 0 until moduleCount) {
                if (qrCode.isDark(row, col)) {
                    darkCount++
                }
            }
        }

        val ratio = (abs((100 * darkCount / moduleCount / moduleCount - 50).toDouble()) / 5).toInt()
        lostPoint += ratio * 10

        return lostPoint
    }

    fun getMode(s: String): Int {
        if (isAlphaNum(s)) {
            if (isNumber(s)) {
                return Mode.MODE_NUMBER
            }
            return Mode.MODE_ALPHA_NUM
        } else if (false) {
            return Mode.MODE_KANJI
        } else {
            return Mode.MODE_8BIT_BYTE
        }
    }

    private fun isNumber(s: String): Boolean {
        for (i in 0 until s.length) {
            val c = s[i]
            if (!('0' <= c && c <= '9')) {
                return false
            }
        }
        return true
    }

    private fun isAlphaNum(s: String): Boolean {
        for (i in 0 until s.length) {
            val c = s[i]
            if (!('0' <= c && c <= '9') && !('A' <= c && c <= 'Z') && " $%*+-./:".indexOf(
                    c
                ) == -1
            ) {
                return false
            }
        }
        return true
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "not implemented, \"SJIS\" encoding required.",
        replaceWith = ReplaceWith("false")
    )
    private fun isKanji(s: String): Boolean {
        return false
//        try {
//            val data: ByteArray = s.toByteArray(charset(jISEncoding))
//
//            var i = 0
//
//            while (i + 1 < data.size) {
//                val c = ((0xff and data[i].toInt()) shl 8) or (0xff and data[i + 1].toInt())
//
//                if (!(0x8140 <= c && c <= 0x9FFC) && !(0xE040 <= c && c <= 0xEBBF)) {
//                    return false
//                }
//
//                i += 2
//            }
//
//            if (i < data.size) {
//                return false
//            }
//
//            return true
//        } catch (e: UnsupportedEncodingException) {
//            throw RuntimeException(e.message)
//        }
    }

    private const val G15 = ((1 shl 10) or (1 shl 8) or (1 shl 5)
            or (1 shl 4) or (1 shl 2) or (1 shl 1) or (1 shl 0))

    private const val G18 = ((1 shl 12) or (1 shl 11) or (1 shl 10)
            or (1 shl 9) or (1 shl 8) or (1 shl 5) or (1 shl 2) or (1 shl 0))

    private const val G15_MASK = ((1 shl 14) or (1 shl 12) or (1 shl 10)
            or (1 shl 4) or (1 shl 1))

    fun getBCHTypeInfo(data: Int): Int {
        var d = data shl 10
        while (getBCHDigit(d) - getBCHDigit(G15) >= 0) {
            d = d xor (G15 shl (getBCHDigit(d) - getBCHDigit(G15)))
        }
        return ((data shl 10) or d) xor G15_MASK
    }

    fun getBCHTypeNumber(data: Int): Int {
        var d = data shl 12
        while (getBCHDigit(d) - getBCHDigit(G18) >= 0) {
            d = d xor (G18 shl (getBCHDigit(d) - getBCHDigit(G18)))
        }
        return (data shl 12) or d
    }

    private fun getBCHDigit(data: Int): Int {
        var data = data
        var digit = 0

        while (data != 0) {
            digit++
            data = data ushr 1
        }

        return digit
    }
}