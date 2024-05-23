package uninit.common

import platform.AudioToolbox.AudioServicesPlaySystemSound
import uninit.common.platform.OsFamily

actual object Platform {
    actual val osFamily: OsFamily
        get() = OsFamily.IOS


    actual fun vibrate(iosIntensity: Float, iosSharpness: Float, androidTime: Int) {
        // TODO: Implement intensity and sharpness
        AudioServicesPlaySystemSound(1352U)
    }

}