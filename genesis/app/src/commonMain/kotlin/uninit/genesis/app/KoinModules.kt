package uninit.genesis.app

import androidx.compose.runtime.Composable
import org.koin.dsl.module
import uninit.common.compose.fytix.ComposableEventBus

object KoinModules {
    @Composable
    fun platformNativeModule() = platformNativeModuleImpl()

    @Composable
    fun globalEmitterModule() = module {
        single {
            ComposableEventBus("GenesisGlobalEventBus")
        }
    }
}

/** PlatformNativeModule is a koin module that provides the following
platform-specific dependencies:

- preferencesManager: `PreferencesManager`
- platformHttpEngineFactory: `HttpClientEngineFactory<*>`

The implementation of this is required to be composable due to the
nature of retrieving the preferencesManager for the Android platform.
*/
@Composable
expect fun platformNativeModuleImpl(): org.koin.core.module.Module