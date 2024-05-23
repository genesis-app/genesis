package uninit.common

import uninit.common.platform.OsFamily

expect object Platform {

    val osFamily: OsFamily

    fun vibrate(iosIntensity: Float, iosSharpness: Float, androidTime: Int)
}