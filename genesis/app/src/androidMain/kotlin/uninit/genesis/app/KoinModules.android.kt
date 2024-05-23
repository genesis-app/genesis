package uninit.genesis.app

import androidx.compose.runtime.Composable
import org.koin.core.module.Module
import org.koin.dsl.module
import uninit.common.compose.preferences.PreferencesManager
import uninit.genesis.app.platform.android.LocalApplicationContext

@Composable
actual fun platformNativeModuleImpl(): Module {
    val context = LocalApplicationContext.current
    return module {
        single {
            context?.let {
                return@single PreferenceManager(it.getSharedPreferences("genesis", Context.MODE_PRIVATE))
            }
            throw IllegalStateException("Application Context is not initialized.")
        }
    }

}