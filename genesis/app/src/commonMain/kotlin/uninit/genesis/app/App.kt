package uninit.genesis.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.navigator.Navigator
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.Napier
import org.koin.compose.getKoin
import org.koin.core.context.startKoin
import uninit.common.compose.preferences.PreferencesManager
import uninit.common.compose.theme.LocalApplicationTheme
import uninit.common.compose.theme.ThemeRegistry
import uninit.genesis.app.ui.screens.LaunchScreen


@Composable
fun App() {
    Napier.base(getAntiLog())
    Napier.d("App started on ${getPlatformName()}")
    val nativeModule = KoinModules.platformNativeModule()
    startKoin {
        modules(nativeModule)
    }
    val themeId by getKoin().get<PreferencesManager>().preference("currentThemeId", "Catppuccin/Mocha/Pink")

    CompositionLocalProvider( LocalApplicationTheme provides ThemeRegistry.getOrDefault(themeId) ) {
        Navigator(LaunchScreen())
    }
}

expect fun getPlatformName(): String

expect fun getAntiLog(): Antilog