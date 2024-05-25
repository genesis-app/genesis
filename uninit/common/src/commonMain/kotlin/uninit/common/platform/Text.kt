package uninit.common.platform

object Text {
    fun stringToByteArrayWithEncoding(text: String, encoding: String): ByteArray {
        return text_StringToByteArrayWithEncoding(text, encoding)
    }
}

internal expect fun text_StringToByteArrayWithEncoding(text: String, encoding: String): ByteArray