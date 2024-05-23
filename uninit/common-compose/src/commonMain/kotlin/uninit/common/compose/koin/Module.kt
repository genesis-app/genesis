package uninit.common.compose.koin

import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.dsl.module

fun uninitModule(
    httpFactory: HttpClientEngineFactory<*>
) = module {
    single { httpModule(httpFactory) }
}