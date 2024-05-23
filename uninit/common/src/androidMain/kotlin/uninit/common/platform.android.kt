package uninit.common

import uninit.common.platform.OsFamily

actual object Platform {
    actual val osFamily: OsFamily
        get() = OsFamily.ANDROID

    actual fun vibrate(iosIntensity: Float, iosSharpness: Float, androidTime: Int) {
        // TODO: This is a stub. Implement this method.
    }

}