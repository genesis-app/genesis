package uninit.ui

import androidx.compose.runtime.compositionLocalOf
import uninit.ui.kit.UIKit
import uninit.ui.theme.ApplicationTheme

val LocalUIKit = compositionLocalOf<UIKit> {
    error("No UIKit provided")
}
val LocalApplicationTheme = compositionLocalOf<ApplicationTheme> {
    error("No ApplicationTheme provided")
}
val LocalMutableWindowTitle = compositionLocalOf<String> {
    ""
}