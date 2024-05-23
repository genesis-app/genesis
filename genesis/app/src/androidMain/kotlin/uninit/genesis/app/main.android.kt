package uninit.genesis.app

import androidx.compose.runtime.Composable
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog

actual fun getPlatformName(): String = "Android"
actual fun getAntiLog(): Antilog = DebugAntilog()

@Composable
fun MainView() = App()
