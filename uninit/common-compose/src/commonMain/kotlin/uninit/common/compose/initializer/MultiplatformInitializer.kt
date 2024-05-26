package uninit.common.compose.initializer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun MultiplatformInitializer(
    modifier: Modifier = Modifier,
    content: @Composable MultiplatformInitializerScope.() -> Unit
) = MultiplatformInitializerScope(modifier).content()