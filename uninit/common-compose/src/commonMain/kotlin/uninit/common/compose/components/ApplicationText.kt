package uninit.common.compose.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import uninit.common.compose.theme.LocalApplicationTheme

@Composable
fun ApplicationText(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, color = LocalApplicationTheme.current.body, textAlign = TextAlign.Center)
}