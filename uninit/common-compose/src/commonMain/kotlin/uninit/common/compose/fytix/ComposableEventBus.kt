package uninit.common.compose.fytix

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import uninit.common.fytix.EventBus

@Suppress("UNCHECKED_CAST")
open class ComposableEventBus(val composableId: String = ""): EventBus() {

    @Composable
    fun <E : Any> compositionLocalOn(event: String, func: Event<E>.(E) -> Unit) {
        val listener = object : EventListener<Event<E>> {
            override val uuid: Int = latestUuid++
            override fun onEvent(event: Event<E>) = event.func(event.data)
        } as EventListener<Event<*>>
        DisposableEffect("EventBus#{{$composableId}}+${listener.uuid}") {
            if (busses.containsKey(event)) {
                busses[event]!!.add(listener)
            } else {
                busses[event] = mutableListOf(listener)
            }
            byId[listener.uuid] = listener
            onDispose {
                off(listener.uuid)
            }
        }
    }
}