package uninit.common.compose

import androidx.compose.runtime.MutableState


fun MutableState<Boolean>.inverse(): MutableState<Boolean> {
    val primary = this

    return object : MutableState<Boolean> {
        override var value: Boolean
            get() = component1()
            set(value) {
                component2().invoke(value)
            }

        override fun component1(): Boolean {
            return !primary.value
        }

        override fun component2(): (Boolean) -> Unit {
            return { value ->
                primary.component2()(!value)
            }
        }

    }
}
