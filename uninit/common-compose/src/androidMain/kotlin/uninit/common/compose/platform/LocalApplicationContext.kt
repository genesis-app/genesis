package uninit.common.compose.platform

import android.content.Context
import androidx.compose.runtime.compositionLocalOf

@Suppress("USELESS_CAST")
val LocalApplicationContext = compositionLocalOf { null as Context? }