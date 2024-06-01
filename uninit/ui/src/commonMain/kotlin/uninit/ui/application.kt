package uninit.ui

import androidx.compose.runtime.Composable
import uninit.ui.application.ApplicationConfigurationScope
import uninit.ui.application.ApplicationScope

@Composable
fun application(
    config: ApplicationConfigurationScope.() -> Unit = {},
    content: @Composable ApplicationScope.() -> Unit)
{

}

@Composable
@Deprecated(level = DeprecationLevel.ERROR, message = "Not intended for use")
private fun example() = application({
    applicationName = "My New Application"
    useWebview = true
}) {
    ui.ElevatedBox {
        ui.Text("Hello, World!")
    }
}