package uninit.genesis.app.state

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalContextMutableWindowTitle = staticCompositionLocalOf { mutableStateOf("Genesis") }