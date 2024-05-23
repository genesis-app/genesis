package uninit.genesis.app

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App() }

actual fun getAntiLog(): Antilog = DebugAntilog()