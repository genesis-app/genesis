package uninit.common.compose.initializer

import androidx.compose.runtime.Composable

interface Step {
    val name: String

    @Composable
    fun StepContent()
}