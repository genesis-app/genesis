package uninit.genesis.app

import org.koin.dsl.module
import uninit.common.compose.preferences.PreferencesManager
import androidx.compose.runtime.Composable

@Composable
actual fun platformNativeModuleImpl() = module {
    single {
        PreferencesManager()
    }
}