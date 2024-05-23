package uninit.genesis.app

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String = "Desktop"


@Composable
fun MainView() {
    App()
}