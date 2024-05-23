package uninit.genesis.app

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import cafe.adriel.voyager.navigator.Navigator
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import uninit.genesis.app.ui.screens.LaunchScreen
import uninit.genesis.app.ui.theme.Theme
import uninit.genesis.app.ui.theme.catppuccin.mocha.CTPMochaPink

val LocalCompositionTheme = compositionLocalOf<Theme> { CTPMochaPink }
@Composable
fun App() {
    Napier.base(getAntiLog())
    Napier.d("App started on ${getPlatformName()}")
    val nativeModule = KoinModules.platformNativeModule()
    startKoin {
        modules(nativeModule)
    }
    CompositionLocalProvider( LocalCompositionTheme provides CTPMochaPink ) {
        Navigator(LaunchScreen())
    }
}

expect fun getPlatformName(): String

expect fun getAntiLog(): Antilog