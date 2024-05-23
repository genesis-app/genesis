package uninit.genesis.app

import androidx.compose.runtime.Composable

object KoinModules {
    @Composable
    fun platformNativeModule() = platformNativeModuleImpl()
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