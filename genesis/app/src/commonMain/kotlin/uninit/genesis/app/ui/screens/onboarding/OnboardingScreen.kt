package uninit.genesis.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import uninit.common.compose.components.ApplicationText
import uninit.common.compose.components.ApplicationTextButton
import uninit.common.compose.fytix.ComposableEventBus
import uninit.common.compose.theme.LocalApplicationTheme
import uninit.genesis.discord.client.GenesisClient
import uninit.genesis.discord.client.gateway.GatewayQRLoginClient
import uninit.genesis.discord.client.gateway.auth.GatewayQRAuthAwaitingApprovalEvent

class OnboardingScreen : Screen {
    private val emitter by lazy {
        ComposableEventBus("OnboardingScreen")
    }
    @Composable
    override fun Content() {
        val theme = LocalApplicationTheme.current
        var token: String? by remember { mutableStateOf(null) }
        emitter.compositionLocalOn("token") { token = it }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.backgroundPane),
            contentAlignment = Alignment.Center

        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .background(
                        theme.secondaryPanes[0],
                        RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = token == null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StandardLoginPanel()
                        OnboardingQRLoginPanel()
                    }
                }
                AnimatedVisibility(
                    visible = token != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LoadingScreen("Logging in...")
                }
            }
        }
    }

    @Composable
    fun OnboardingQRLoginPanel() {
        var state by remember { mutableStateOf(QRLoginState.AwaitingQRCode) }
        var qrLink by remember { mutableStateOf("") }
        var token by remember { mutableStateOf("") }
        var partialUser: GatewayQRAuthAwaitingApprovalEvent? by remember { mutableStateOf(null) }
        val qrAuthClient = GatewayQRLoginClient(getKoin().get())

        var top: @Composable (Modifier) -> Unit by remember { mutableStateOf({}) }
        var middle: @Composable (Modifier) -> Unit by remember { mutableStateOf({}) }
        var bottom: @Composable (Modifier) -> Unit by remember { mutableStateOf({}) }

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
        qrAuthClient.onToken {
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
        when (state) {
            QRLoginState.AwaitingQRCode -> {
                top = { ApplicationText("Loading QR code!", modifier = it) }
                middle = { Box(modifier = it) {} }
                bottom = { ApplicationText("Connecting to discord...", modifier = it) }
            }
            QRLoginState.AwaitingScan -> {
                top = { Box(modifier = it) {} }
                middle = { QRCode(qrLink, modifier = it) }
                bottom = { ApplicationText("Scan the QR code with your Discord app!", modifier = it) }
            }
            QRLoginState.AwaitingApproval,
            QRLoginState.ExchangingTicket,
            QRLoginState.TokenReceived -> {
                top = { modifier ->
                    partialUser?.let {
                        ApplicationText("Welcome back,  ${it.userName}!", modifier = modifier)
                    }
                }
                middle = { modifier ->
                    Box(
                        modifier = modifier
                            .fillMaxSize(0.7f)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        partialUser?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it.userAvatarUri),
                                contentDescription = "User Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: CircularProgressIndicator(modifier)
                    }
                }
                bottom = {
                    when (state) {
                        QRLoginState.AwaitingApproval -> ApplicationText("Please approve the login request on your device.", modifier = it)
                        QRLoginState.ExchangingTicket -> ApplicationText("Confirming with Discord...", modifier = it)
                        QRLoginState.TokenReceived -> ApplicationText("Logging in...", modifier = it)
                        else -> {}
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .background(
                    LocalApplicationTheme.current.secondaryPanes[1],
                    RoundedCornerShape(10.dp)
                )
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier.weight(0.1f).fillMaxHeight()) {top(Modifier)}
                Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {middle(Modifier)}
                Box(modifier = Modifier.weight(0.3f).fillMaxHeight()) {bottom(Modifier)}
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
    fun QRCode(link: String, modifier: Modifier = Modifier) {
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
                AsyncImage(it, "QR Code", modifier = modifier)
            }
        }
    }

    //#region Username/Password or Token authentication
    enum class AuthMethod {
        UsernamePassword,
        Token
    }
    @Composable
    fun StandardLoginPanel() {
        Column(
            modifier = Modifier.fillMaxSize(0.5f).padding(16.dp).background(LocalApplicationTheme.current.secondaryPanes[0]),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var authMethod = remember { mutableStateOf(AuthMethod.UsernamePassword) }
            LoginMethodSelector(authMethod)
            when (authMethod.value) {
                AuthMethod.UsernamePassword -> UsernamePasswordLogin()
                AuthMethod.Token -> TokenLogin()
            }

        }
    }
    @Composable
    fun LoginMethodSelector(
        state: MutableState<AuthMethod>
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            ApplicationTextButton("Email or SMS", modifier = Modifier.weight(0.6f)) {
                state.value = AuthMethod.UsernamePassword
            }
            Spacer(modifier = Modifier.width(8.dp))
            ApplicationTextButton("Token", modifier = Modifier.weight(0.4f)) {
                state.value = AuthMethod.Token
            }
        }
    }
    @Composable
    fun UsernamePasswordLogin() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        Column {
//            TextField(username, { username = it })
//            TextField(password, { password = it })
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                ApplicationTextButton("Login", modifier = Modifier.fillMaxWidth(0.5f)) {
                    Napier.v("Logging in with username/password")
                }
            }

        }
    }
    @Composable
    fun TokenLogin() {
        var token by remember { mutableStateOf("") }
        Column {
//            TextField(token, { token = it })
            Row(
                modifier = Modifier.fillMaxWidth(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                ApplicationTextButton("Login with Token", modifier = Modifier.fillMaxWidth(0.5f)) {
                    Napier.v("Logging in with token")
                }
            }
        }
    }
    //#endregion
}


enum class QRLoginState {
    AwaitingQRCode,
    AwaitingScan,
    AwaitingApproval,
    ExchangingTicket,
    TokenReceived
}
