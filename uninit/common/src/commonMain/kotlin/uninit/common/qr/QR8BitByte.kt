package uninit.common.qr

/**
 * QR8BitByte
 * @author Kazuhiko Arase, Ported by Aenri Lovehart
 */
internal class QR8BitByte(data: String) : QRData(Mode.MODE_8BIT_BYTE, data) {
    override fun write(buffer: BitBuffer) {
        throw RuntimeException("not implemented, \"SJIS\" encoding required.")
//        try {
//            val data = data.toByteArray()
//
//            for (i in data.indices) {
//                buffer.put(data[i].toInt(), 8)
//            }
//        } catch (e: Exception) {
//            throw RuntimeException(e.message)
//        }
    }

    override val length: Int
        get() {
            throw RuntimeException("not implemented, \"SJIS\" encoding required.")
//            try {
//                return getData().getBytes(QRCode.get8BitByteEncoding()).length
//            } catch (e: UnsupportedEncodingException) {
//                throw java.lang.RuntimeException(e.message)
//            }
        }
}