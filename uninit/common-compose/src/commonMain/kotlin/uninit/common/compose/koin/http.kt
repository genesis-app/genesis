package uninit.common.compose.koin

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.compose.getKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module


internal fun httpModule(factory: HttpClientEngineFactory<*>) = module {
    single(named("uninitCommonComposeHttp")) { HttpClient(factory) }
}

@Composable
internal fun getHttpClient() = getKoin().get<HttpClient>(named("uninitCommonComposeHttp"))