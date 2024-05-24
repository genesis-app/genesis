package uninit.common.qr

import kotlin.jvm.JvmOverloads

/**
 * Polynomial
 * @author Kazuhiko Arase, Ported by Aenri Lovehart
 */
internal class Polynomial @JvmOverloads constructor(num: IntArray, shift: Int = 0) {
    private val num: IntArray

    init {
        var offset = 0

        while (offset < num.size && num[offset] == 0) {
            offset++
        }

        this.num = IntArray(num.size - offset + shift)
        num.copyInto(this.num, num.size - offset, 0, offset)
    }

    fun get(index: Int): Int {
        return num[index]
    }

    val length: Int
        get() = num.size

    override fun toString(): String {
        val buffer = StringBuilder()

        for (i in 0 until length) {
            if (i > 0) {
                buffer.append(",")
            }
            buffer.append(get(i))
        }

        return buffer.toString()
    }

    fun toLogString(): String {
        val buffer = StringBuilder()

        for (i in 0 until length) {
            if (i > 0) {
                buffer.append(",")
            }
            buffer.append(QRMath.glog(get(i)))
        }

        return buffer.toString()
    }

    fun multiply(e: Polynomial): Polynomial {
        val num = IntArray(length + e.length - 1)

        for (i in 0 until length) {
            for (j in 0 until e.length) {
                num[i + j] = num[i + j] xor QRMath.gexp(QRMath.glog(get(i)) + QRMath.glog(e.get(j)))
            }
        }

        return Polynomial(num)
    }

    fun mod(e: Polynomial): Polynomial {
        if (length - e.length < 0) {
            return this
        }

        // 最上位桁の比率
        val ratio: Int = QRMath.glog(get(0)) - QRMath.glog(e.get(0))

        // コピー作成
        val num = IntArray(length)
        for (i in 0 until length) {
            num[i] = get(i)
        }


        // 引き算して余りを計算
        for (i in 0 until e.length) {
            num[i] = num[i] xor QRMath.gexp(QRMath.glog(e.get(i)) + ratio)
        }

        // 再帰計算
        return Polynomial(num).mod(e)
    }
}