package uninit.genesis.app.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.getKoin
import uninit.common.compose.preferences.PreferencesManager
import uninit.genesis.app.ui.screens.onboarding.OnboardingScreen

class LaunchScreen : Screen {

    @Composable
    override fun Content() {
        val firstLaunch by getKoin().get<PreferencesManager>().preference("completedOnboarding", false)

        if (!firstLaunch) {
            LocalNavigator.currentOrThrow.push(OnboardingScreen())
        } else {
            // Navigate to main screen
        }
    }
}