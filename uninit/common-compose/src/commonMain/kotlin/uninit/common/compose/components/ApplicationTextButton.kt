package uninit.common.compose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import uninit.common.compose.theme.LocalApplicationTheme

enum class ApplicationTextButtonStyle {
    Standard,
    TransparentBackground,
    Underlined
}
internal fun ApplicationTextButtonStyle.applyToBackground(color: Color) = when (this) {
    ApplicationTextButtonStyle.Standard -> color
    ApplicationTextButtonStyle.TransparentBackground -> Color.Transparent
    ApplicationTextButtonStyle.Underlined -> Color.Transparent
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationTextButton(
    text: String,
    style: ApplicationTextButtonStyle = ApplicationTextButtonStyle.Underlined,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val themeTextColor = LocalApplicationTheme.current.body
    var textColor by remember { mutableStateOf(themeTextColor) }
    var hovered by remember { mutableStateOf(false) }
    var mouseDown by remember { mutableStateOf(false) }

    val underlineWidth by animateFloatAsState(
        targetValue = if (mouseDown) 0.6F else if (hovered) 0.8F else 1F
    )
    val textOpacity by animateFloatAsState(
        targetValue = if (mouseDown) 0.4F else if (hovered) 0.5F else 1F
    )
    Box(
        modifier = Modifier
            .then(modifier)
            .onPointerEvent(PointerEventType.Enter) {
                hovered = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                hovered = false
            }
            .onPointerEvent(PointerEventType.Press) {
                mouseDown = true
            }
            .onPointerEvent(PointerEventType.Release) {
                mouseDown = false

                onClick()
            }
            .background(
                color = style.applyToBackground(LocalApplicationTheme.current.secondaryPanes.second),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                color = textColor.copy(alpha = textOpacity),
                modifier = Modifier
            )
            if (style == ApplicationTextButtonStyle.Underlined)
                HorizontalDivider(
                    color = textColor.copy(alpha = textOpacity),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth(underlineWidth)
                )
        }
    }
}