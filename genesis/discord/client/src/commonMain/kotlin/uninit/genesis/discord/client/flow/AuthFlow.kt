package uninit.genesis.discord.client.flow

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class AuthFlow(
    private val http: HttpClient,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    init {

        coroutineScope.launch {

        }


    }

}