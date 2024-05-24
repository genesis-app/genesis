package uninit.genesis.app

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.module.Module
import org.koin.dsl.module
import uninit.common.compose.preferences.PreferencesManager
import androidx.compose.runtime.Composable

@Composable
actual fun platformNativeModuleImpl() = module {
    single {
        PreferencesManager(".genesis.json")
    }
    single<HttpClientEngineFactory<*>> {
        OkHttp
    }
}