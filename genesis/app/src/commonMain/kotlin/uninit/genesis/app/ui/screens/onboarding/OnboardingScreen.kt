package uninit.genesis.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import uninit.genesis.app.LocalCompositionTheme

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
            Text(modifier = Modifier.align(Alignment.Center), text = "Onboarding", color = theme.text)
        }
    }
}