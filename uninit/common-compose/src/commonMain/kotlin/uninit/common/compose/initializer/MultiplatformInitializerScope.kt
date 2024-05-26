package uninit.common.compose.initializer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.setting.WebSettings

class MultiplatformInitializerScope(
    val modifier: Modifier
) {

}


//@Composable
//expect fun MultiplatformInitializerScope.loadWebview(
//    view: @Composable (
//        Boolean, // restartRequired
//        Boolean, // isWebviewReady
//        Float, // downloadingProgress
//    ) -> Unit
//)