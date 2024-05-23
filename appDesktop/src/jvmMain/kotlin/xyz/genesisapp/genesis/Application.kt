package uninit.genesis

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import uninit.genesis.app.MainView
import uninit.genesis.app.state.LocalContextMutableWindowTitle


class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            application {
                content()
            }
        }
    }
}
@Composable
@Preview
fun ApplicationScope.content() {
    CompositionLocalProvider(
        LocalContextMutableWindowTitle provides mutableStateOf("Genesis"),
    ) {
        Window(
            onCloseRequest = ::exitApplication,
            title = LocalContextMutableWindowTitle.current.component1(),
        ) {
            MainView()
        }
    }
}