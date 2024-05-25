package uninit.common.platform

import java.nio.charset.Charset

internal actual fun text_StringToByteArrayWithEncoding(text: String, encoding: String): ByteArray {
    return text.toByteArray(Charset.forName(encoding))
}