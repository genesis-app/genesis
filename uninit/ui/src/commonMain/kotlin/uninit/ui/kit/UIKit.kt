package uninit.ui.kit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle


interface UIKit {
    @Composable
    fun Text(
        text: String,
        style: TextStyle = TextStyle.Default,
        modifier: Modifier = Modifier
    )
    @Composable
    fun ElevatedBox(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    )
}