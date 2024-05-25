package uninit.genesis.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import io.github.aakira.napier.Napier
import io.ktor.client.engine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import org.koin.compose.getKoin
import qrcode.QRCode
import qrcode.color.Colors
import uninit.common.compose.theme.LocalApplicationTheme
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.gateway.GatewayQRLoginClient
import uninit.genesis.discord.client.gateway.auth.GatewayQRAuthAwaitingApprovalEvent

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        val theme = LocalApplicationTheme.current
//        val windowSize = currentWindowAdaptiveInfo().windowSizeClass
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.backgroundPane),
            contentAlignment = Alignment.Center

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
                    partialUser?.let { UserPartialInfo(it, state == QRLoginState.AwaitingApproval) }
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
            Text(text, color = LocalApplicationTheme.current.body)
        }

    }
    @Composable
    fun QRCode(link: String) {
        Napier.v("QRCode: $link")
        var qr by remember<MutableState<ByteArray?>> { mutableStateOf(null) }
        var size by remember { mutableStateOf(IntSize.Zero) }
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(0.5f)
                .padding(16.dp)
                .onSizeChanged {
                    size = it
                },
            contentAlignment = Alignment.Center
        ) {
            val color = LocalApplicationTheme.current.body
            LaunchedEffect(link) {
                color.let {
                    qr = QRCode
                        .ofRoundedSquares()
                        .withColor(Colors.rgba(
                            (it.red * 255F).toInt(),
                            (it.green * 255F).toInt(),
                            (it.blue * 255F).toInt(),
                            (it.alpha * 255F).toInt()
                        ))
                        .withBackgroundColor(Colors.TRANSPARENT)
                        .withRadius(10)
                        .withSize(10)
                        .build(link)
                        .renderToBytes()
                }
            }
            qr?.let {
                AsyncImage(it, "QR Code")
            }
        }
    }


    @Composable
    fun UserPartialInfo(
        partialUser: GatewayQRAuthAwaitingApprovalEvent,
        sayApprove: Boolean
    ) {
        Napier.v("UserPartialInfo: $partialUser")
        val painter = rememberAsyncImagePainter(partialUser.userAvatarUri)
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ThemedText("Welcome Back, ${partialUser.userName}!")
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(100.dp),
                            color = LocalApplicationTheme.current.accent
                        )
                    }
                    is AsyncImagePainter.State.Error -> {
                        ThemedText("Error loading avatar")
                    }
                    is AsyncImagePainter.State.Success -> {
                        Image(
                            painter = painter,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(
                                    RoundedCornerShape(50.dp)
                                )
                        )
                    }

                    AsyncImagePainter.State.Empty -> TODO()
                }
            if (sayApprove) ThemedText("Please approve the login request on your device.")
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
            ThemedText("Token Received, testing...")
            messages.forEach {
                ThemedText(it)
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

    @Composable
    fun ThemedText(text: String) {
        Text(text, color = LocalApplicationTheme.current.body)
    }


}

private fun Bitmap.Companion.decodeByteArray(it: ByteArray): Bitmap {
    val bitmap = Bitmap()
    val image = Image.makeFromEncoded(it)
    bitmap.setImageInfo(image.imageInfo)
    image.readPixels(dst = bitmap)
    return bitmap
}

enum class QRLoginState {
    AwaitingQRCode,
    AwaitingScan,
    AwaitingApproval,
    ExchangingTicket,
    TokenReceived
}
