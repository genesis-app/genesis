package uninit.common.qr

import kotlin.jvm.JvmOverloads
import kotlin.math.max


class QRCode() {
    /**
     * 型番を取得する。
     * @return 型番
     */
    /**
     * 型番を設定する。
     * @param typeNumber 型番
     */
    var typeNumber: Int = 1

    lateinit var modules: Array<Array<Boolean?>>

    /**
     * モジュール数を取得する。
     */
    var moduleCount: Int = 0
        private set

    /**
     * 誤り訂正レベルを取得する。
     * @return 誤り訂正レベル
     * @see ErrorCorrectionLevel
     */
    /**
     * 誤り訂正レベルを設定する。
     * @param errorCorrectionLevel 誤り訂正レベル
     * @see ErrorCorrectionLevel
     */
    var errorCorrectionLevel: Int

    private val qrDataList: MutableList<QRData>

    /**
     * モードを指定してデータを追加する。
     * @param data データ
     * @param mode モード
     * @see Mode
     */
    /**
     * データを追加する。
     * @param data データ
     */
    @JvmOverloads
    fun addData(data: String, mode: Int = QRUtil.getMode(data)) {
        when (mode) {
            Mode.MODE_NUMBER -> addData(QRNumber(data))
            Mode.MODE_ALPHA_NUM -> addData(QRAlphaNum(data))
            Mode.MODE_8BIT_BYTE -> addData(QR8BitByte(data))
            Mode.MODE_KANJI -> addData(QRKanji(data))
            else -> throw IllegalArgumentException("mode:$mode")
        }
    }

    /**
     * データをクリアする。
     * <br></br>addData で追加されたデータをクリアします。
     */
    fun clearData() {
        qrDataList.clear()
    }

    internal fun addData(qrData: QRData) {
        qrDataList.add(qrData)
    }

    protected val dataCount: Int
        get() = qrDataList.size

    protected fun getData(index: Int): QRData {
        return qrDataList[index]
    }

    /**
     * 暗モジュールかどうかを取得する。
     * @param row 行 (0 ～ モジュール数 - 1)
     * @param col 列 (0 ～ モジュール数 - 1)
     */
    fun isDark(row: Int, col: Int): Boolean {
        if (modules[row][col] != null) {
            return modules[row][col]!!
        } else {
            return false
        }
    }

    /**
     * QRコードを作成する。
     */
    fun make() {
        make(false, bestMaskPattern)
    }

    private val bestMaskPattern: Int
        get() {
            var minLostPoint = 0
            var pattern = 0

            for (i in 0..7) {
                make(true, i)

                val lostPoint: Int = QRUtil.getLostPoint(this)

                if (i == 0 || minLostPoint > lostPoint) {
                    minLostPoint = lostPoint
                    pattern = i
                }
            }

            return pattern
        }

    /**
     *
     */
    private fun make(test: Boolean, maskPattern: Int) {
        // モジュール初期化

        moduleCount = typeNumber * 4 + 17
        modules = Array(moduleCount) {
            arrayOfNulls(
                moduleCount
            )
        }

        // 位置検出パターン及び分離パターンを設定
        setupPositionProbePattern(0, 0)
        setupPositionProbePattern(moduleCount - 7, 0)
        setupPositionProbePattern(0, moduleCount - 7)

        setupPositionAdjustPattern()
        setupTimingPattern()

        setupTypeInfo(test, maskPattern)

        if (typeNumber >= 7) {
            setupTypeNumber(test)
        }

        val dataArray = qrDataList.toTypedArray<QRData>()

        val data = createData(typeNumber, errorCorrectionLevel, dataArray)

        mapData(data, maskPattern)
    }

    private fun mapData(data: ByteArray, maskPattern: Int) {
        var inc = -1
        var row = moduleCount - 1
        var bitIndex = 7
        var byteIndex = 0

        var col = moduleCount - 1
        while (col > 0) {
            if (col == 6) col--

            while (true) {
                for (c in 0..1) {
                    if (modules[row][col - c] == null) {
                        var dark = false

                        if (byteIndex < data.size) {
                            dark = (((data[byteIndex].toInt() ushr bitIndex) and 1) == 1)
                        }

                        val mask: Boolean = QRUtil.getMask(maskPattern, row, col - c)

                        if (mask) {
                            dark = !dark
                        }

                        modules[row][col - c] = dark
                        bitIndex--

                        if (bitIndex == -1) {
                            byteIndex++
                            bitIndex = 7
                        }
                    }
                }

                row += inc

                if (row < 0 || moduleCount <= row) {
                    row -= inc
                    inc = -inc
                    break
                }
            }
            col -= 2
        }
    }

    /**
     * 位置合わせパターンを設定
     */
    private fun setupPositionAdjustPattern() {
        val pos: IntArray = QRUtil.getPatternPosition(typeNumber)

        for (i in pos.indices) {
            for (j in pos.indices) {
                val row = pos[i]
                val col = pos[j]

                if (modules[row][col] != null) {
                    continue
                }

                for (r in -2..2) {
                    for (c in -2..2) {
                        if ((r == -2) || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0)) {
                            modules[row + r][col + c] = true
                        } else {
                            modules[row + r][col + c] = false
                        }
                    }
                }
            }
        }
    }

    /**
     * 位置検出パターンを設定
     */
    private fun setupPositionProbePattern(row: Int, col: Int) {
        for (r in -1..7) {
            for (c in -1..7) {
                if ((row + r <= -1 || moduleCount <= row + r) || col + c <= -1 || moduleCount <= col + c) {
                    continue
                }

                if (((0 <= r) && r <= 6 && (c == 0 || c == 6))
                    || ((0 <= c) && c <= 6 && (r == 0 || r == 6))
                    || ((((2 <= r) && r <= 4) && 2 <= c) && c <= 4)
                ) {
                    modules[row + r][col + c] = true
                } else {
                    modules[row + r][col + c] = false
                }
            }
        }
    }

    /**
     * タイミングパターンを設定
     */
    private fun setupTimingPattern() {
        for (r in 8 until moduleCount - 8) {
            if (modules[r][6] != null) {
                continue
            }
            modules[r][6] = r % 2 == 0
        }
        for (c in 8 until moduleCount - 8) {
            if (modules[6][c] != null) {
                continue
            }
            modules[6][c] = c % 2 == 0
        }
    }

    /**
     * 型番を設定
     */
    private fun setupTypeNumber(test: Boolean) {
        val bits: Int = QRUtil.getBCHTypeNumber(typeNumber)

        for (i in 0..17) {
            val mod = !test && ((bits shr i) and 1) == 1
            modules[i / 3][(i % 3 + moduleCount) - 8 - 3] = mod
        }

        for (i in 0..17) {
            val mod = !test && ((bits shr i) and 1) == 1
            modules[(i % 3 + moduleCount) - 8 - 3][i / 3] = mod
        }
    }

    /**
     * 形式情報を設定
     */
    private fun setupTypeInfo(test: Boolean, maskPattern: Int) {
        val data = (errorCorrectionLevel shl 3) or maskPattern
        val bits: Int = QRUtil.getBCHTypeInfo(data)

        // 縦方向
        for (i in 0..14) {
            val mod = !test && ((bits shr i) and 1) == 1

            if (i < 6) {
                modules[i][8] = mod
            } else if (i < 8) {
                modules[i + 1][8] = mod
            } else {
                modules[moduleCount - 15 + i][8] = mod
            }
        }

        // 横方向
        for (i in 0..14) {
            val mod = !test && ((bits shr i) and 1) == 1

            if (i < 8) {
                modules[8][moduleCount - i - 1] = mod
            } else if (i < 9) {
                modules[8][15 - i - 1 + 1] = mod
            } else {
                modules[8][15 - i - 1] = mod
            }
        }

        // 固定
        modules[moduleCount - 8][8] = !test
    }

    /**
     * コンストラクタ
     * <br></br>型番1, 誤り訂正レベルH のQRコードのインスタンスを生成します。
     * @see ErrorCorrectionLevel
     */
    init {
        this.errorCorrectionLevel = ErrorCorrectionLevel.H
        this.qrDataList = ArrayList<QRData>(1)
    }

    companion object {
        private val PAD0 = 0xEC

        private val PAD1 = 0x11

        fun createData(typeNumber: Int, errorCorrectionLevel: Int, dataArray: Array<QRData>): ByteArray {
            val rsBlocks: Array<RSBlock> = RSBlock.getRSBlocks(typeNumber, errorCorrectionLevel)

            val buffer = BitBuffer()

            for (i in dataArray.indices) {
                val data = dataArray[i]
                buffer.put(data.mode, 4)
                buffer.put(data.length, data.getLengthInBits(typeNumber))
                data.write(buffer)
            }

            // 最大データ数を計算
            var totalDataCount = 0
            for (i in rsBlocks.indices) {
                totalDataCount += rsBlocks[i].dataCount
            }

            if (buffer.lengthInBits > totalDataCount * 8) {
                throw IllegalArgumentException(
                    ("code length overflow. ("
                            + buffer.lengthInBits
                            + ">") + totalDataCount * 8 + ")"
                )
            }

            // 終端コード
            if (buffer.lengthInBits + 4 <= totalDataCount * 8) {
                buffer.put(0, 4)
            }

            // padding
            while (buffer.lengthInBits % 8 != 0) {
                buffer.put(false)
            }

            // padding
            while (true) {
                if (buffer.lengthInBits >= totalDataCount * 8) {
                    break
                }
                buffer.put(PAD0, 8)

                if (buffer.lengthInBits >= totalDataCount * 8) {
                    break
                }
                buffer.put(PAD1, 8)
            }

            return createBytes(buffer, rsBlocks)
        }

        private fun createBytes(buffer: BitBuffer, rsBlocks: Array<RSBlock>): ByteArray {
            var offset = 0

            var maxDcCount = 0
            var maxEcCount = 0

            val dcdata = arrayOfNulls<IntArray>(rsBlocks.size)
            val ecdata = arrayOfNulls<IntArray>(rsBlocks.size)

            for (r in rsBlocks.indices) {
                val dcCount: Int = rsBlocks[r].dataCount
                val ecCount: Int = rsBlocks[r].totalCount - dcCount

                maxDcCount = max(maxDcCount.toDouble(), dcCount.toDouble()).toInt()
                maxEcCount = max(maxEcCount.toDouble(), ecCount.toDouble()).toInt()

                dcdata[r] = IntArray(dcCount)
                for (i in dcdata[r]!!.indices) {
                    dcdata[r]!![i] = 0xff and buffer.buffer[i + offset].toInt()
                }
                offset += dcCount

                val rsPoly: Polynomial = QRUtil.getErrorCorrectPolynomial(ecCount)
                val rawPoly = Polynomial(dcdata[r]!!, rsPoly.length - 1)

                val modPoly = rawPoly.mod(rsPoly)
                ecdata[r] = IntArray(rsPoly.length - 1)
                for (i in ecdata[r]!!.indices) {
                    val modIndex = i + modPoly.length - ecdata[r]!!.size
                    ecdata[r]!![i] = if ((modIndex >= 0)) modPoly.get(modIndex) else 0
                }
            }

            var totalCodeCount = 0
            for (i in rsBlocks.indices) {
                totalCodeCount += rsBlocks[i].totalCount
            }

            val data = ByteArray(totalCodeCount)

            var index = 0

            for (i in 0 until maxDcCount) {
                for (r in rsBlocks.indices) {
                    if (i < dcdata[r]!!.size) {
                        data[index++] = dcdata[r]!![i].toByte()
                    }
                }
            }

            for (i in 0 until maxEcCount) {
                for (r in rsBlocks.indices) {
                    if (i < ecdata[r]!!.size) {
                        data[index++] = ecdata[r]!![i].toByte()
                    }
                }
            }

            return data
        }

        /**
         * 最小の型番となる QRCode を作成する。
         * @param data データ
         * @param errorCorrectionLevel 誤り訂正レベル
         */
        fun getMinimumQRCode(data: String, errorCorrectionLevel: Int): QRCode {
            val mode: Int = QRUtil.getMode(data)

            val qr = QRCode()
            qr.errorCorrectionLevel = errorCorrectionLevel
            qr.addData(data, mode)

            val length = qr.getData(0).length

            for (typeNumber in 1..10) {
                if (length <= QRUtil.getMaxLength(typeNumber, mode, errorCorrectionLevel)) {
                    qr.typeNumber = typeNumber
                    break
                }
            }

            qr.make()

            return qr
        }

        private var _8BitByteEncoding: String = QRUtil.jISEncoding
        fun set8BitByteEncoding(_8BitByteEncoding: String) {
            Companion._8BitByteEncoding = _8BitByteEncoding
        }

        fun get8BitByteEncoding(): String {
            return _8BitByteEncoding
        }
    }
}