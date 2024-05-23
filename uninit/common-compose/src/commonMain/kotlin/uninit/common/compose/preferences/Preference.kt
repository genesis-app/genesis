package uninit.common.compose.preferences

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class Preference<T>(
    private val key: String,
    private val defaultValue: T,
    private val getter: (String, T) -> T,
    private val setter: (T) -> Unit,
    internal val state: MutableState<T> = mutableStateOf(getter(key, defaultValue))
) : MutableState<T> by state {
    override var value: T
        get() = state.component1()
        set(value) = component2().invoke(value)
    override fun component1(): T {
        return value
    }
    override fun component2(): (T) -> Unit {
        return {
            state.component2().invoke(it)
            setter(it)
        }
    }

    operator fun getValue(thisRef: Any?, property: Any?): T {
        return value
    }
    operator fun setValue(thisRef: Any?, property: Any?, value: T) {
        this.value = value
    }
}