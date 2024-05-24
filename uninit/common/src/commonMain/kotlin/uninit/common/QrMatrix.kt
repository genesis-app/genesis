package uninit.common

import uninit.common.qr.ErrorCorrectionLevel
import uninit.common.qr.QRCode

/**
 * Simple ASCII QR Matrix, represented as a 2D list of booleans.
 * Low error correction, Simple wrapper around kazuhikoarase/qr-code-generator
 * @param data The data to encode in the QR matrix. Must be ASCII.
 */
class QrMatrix(val data: String) {
    private val qrCode = QRCode.getMinimumQRCode(data, ErrorCorrectionLevel.L)

    val matrix: List<List<Boolean>> = qrCode.modules.map { row ->
        row.map { it!! }
    }

    val width: Int = matrix.size
    val height: Int = matrix[0].size

    fun at(x: Int, y: Int): Boolean {
        return matrix[x][y]
    }

}
