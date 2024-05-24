package uninit.genesis.app.ui.screens.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.github.aakira.napier.Napier
import io.ktor.client.engine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import uninit.common.QrMatrix
import uninit.genesis.app.LocalCompositionTheme
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.gateway.GatewayQRLoginClient
import uninit.genesis.discord.client.gateway.auth.GatewayQRAuthAwaitingApprovalEvent

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        val theme = LocalCompositionTheme.current
//        val windowSize = currentWindowAdaptiveInfo().windowSizeClass
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.base)
        ) {
            OnboardingQRLoginPage()
        }
    }

    @Composable
    fun OnboardingQRLoginPage() {
        var state by remember { mutableStateOf(QRLoginState.AwaitingQRCode) }
        var qrLink by remember { mutableStateOf("") }
        var token by remember { mutableStateOf("") }
        var partialUser: GatewayQRAuthAwaitingApprovalEvent? by remember { mutableStateOf(null) }

        val qrAuthClient = GatewayQRLoginClient(getKoin().get())
        qrAuthClient.onAwaitingApproval {
            Napier.v("AwaitingApproval: $it")
            state = QRLoginState.AwaitingApproval
            partialUser = it
        }
        qrAuthClient.onLoading {
            Napier.v("Loading: $it")
            if (it.text == "Exchanging token...")
                state = QRLoginState.ExchangingTicket
            else
                state = QRLoginState.AwaitingQRCode
        }
        qrAuthClient.onceToken {
            Napier.v("Token: $it")
            state = QRLoginState.TokenReceived
            token = it.token
        }
        qrAuthClient.onFingerprintAvailable {
            Napier.v("Fingerprint: ${it.fingerprint}")
            state = QRLoginState.AwaitingScan
            qrLink = "https://discord.com/ra/${it.fingerprint}"
        }
        qrAuthClient.on<String>("raw") {
            Napier.v("Raw: $it")
        }
        LaunchedEffect(Unit) {
            qrAuthClient.connect()
        }
        Column(
            modifier = Modifier.fillMaxSize(0.7f).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                QRLoginState.AwaitingQRCode -> {
                    LoadingScreen("Loading QR Login...")
                }
                QRLoginState.AwaitingScan -> {
                    QRCode(qrLink)
                }
                QRLoginState.AwaitingApproval,
                QRLoginState.ExchangingTicket -> {
                    partialUser?.let { UserPartialInfo(it) }
                    if (state == QRLoginState.ExchangingTicket) LoadingScreen("Exchanging ticket...")
                }
                QRLoginState.TokenReceived -> {
                    TestTokenReceived(token)
                }
            }
        }
    }

    @Composable
    fun LoadingScreen(text: String) {
        Napier.v("LoadingScreen: $text")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = LocalCompositionTheme.current.text)
        }

    }
    @Composable
    fun QRCode(link: String) {
        Napier.v("QRCode: $link")
        val qrMatrix by remember { mutableStateOf(QrMatrix(link)) }
        val primary = LocalCompositionTheme.current.primary
        Box(
            modifier = Modifier.aspectRatio(1f).fillMaxWidth(0.5f).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / qrMatrix.width
                qrMatrix.matrix.forEachIndexed { x, row ->
                    row.forEachIndexed { y, value ->
                        if (value) {
                            drawRect(
                                color = primary,
                                topLeft = Offset(x * cellSize, y * cellSize),
                                size = Size(cellSize, cellSize)
                            )
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun UserPartialInfo(partialUser: GatewayQRAuthAwaitingApprovalEvent) {
        Napier.v("UserPartialInfo: $partialUser")
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("User: ${partialUser.userName}")
            Text("ID: ${partialUser.userId}")
            Text("AvatarUri: ${partialUser.userAvatarUri}")
        }
    }

    @Composable
    fun TestTokenReceived(token: String) {
        val httpClientEngineFactory = getKoin().get<HttpClientEngineFactory<*>>()
        val messages by remember { mutableStateOf(mutableListOf<String>()) }
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Token Received, testing...")
            messages.forEach {
                Text(it)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val client = GenesisClient(httpClientEngineFactory)
            val res = client.tryTokenLogin(token)
            res.map {
                messages.add("Logged in as ${it.username}")
            }.mapError {
                messages.add("Error: $it")
            }
        }
    }

}
enum class QRLoginState {
    AwaitingQRCode,
    AwaitingScan,
    AwaitingApproval,
    ExchangingTicket,
    TokenReceived
}