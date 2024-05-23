package uninit.common

import platform.Foundation.NSDate
import platform.Foundation.date
import platform.Foundation.timeIntervalSince1970

actual fun getTimeInMillis(): Long {
    val secs = NSDate.date().timeIntervalSince1970()
    return (secs * 1000 + if (secs > 0) 0.5 else -0.5).toLong()
}
