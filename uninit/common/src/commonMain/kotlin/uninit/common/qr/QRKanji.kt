package uninit.common.qr

import QRUtil


internal class QRKanji(data: String?) : QRData(Mode.MODE_KANJI, data!!) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun write(buffer: BitBuffer) {
        throw RuntimeException("not implemented, \"SJIS\" encoding required.")
//        try {
//            val data: ByteArray = data.getBytes(QRUtil.getJISEncoding())
//
//            var i = 0
//
//            while (i + 1 < data.size) {
//                var c = ((0xff and data[i].toInt()) shl 8) or (0xff and data[i + 1].toInt())
//
//                c -= if (0x8140 <= c && c <= 0x9FFC) {
//                    0x8140
//                } else if (0xE040 <= c && c <= 0xEBBF) {
//                    0xC140
//                } else {
//                    throw IllegalArgumentException(
//                        "illegal char at " + (i + 1) + "/" + c.toHexString()
//                    )
//                }
//
//                c = ((c ushr 8) and 0xff) * 0xC0 + (c and 0xff)
//
//                buffer.put(c, 13)
//
//                i += 2
//            }
//
//            if (i < data.size) {
//                throw java.lang.IllegalArgumentException("illegal char at " + (i + 1))
//            }
//        } catch (e: Exception) {
//            throw RuntimeException(e.message)
//        }
    }

    override val length: Int
        get() {
            throw RuntimeException("not implemented, \"SJIS\" encoding required.")
//            try {
//                return data.getBytes(QRUtil.getJISEncoding()).size / 2
//            } catch (e: UnsupportedEncodingException) {
//                throw java.lang.RuntimeException(e.message)
//            }
        }
}