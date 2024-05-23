package uninit.common.fytix

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("UNCHECKED_CAST")
open class EventBus {

    interface EventListener<E> {
        val uuid: Int
        fun onEvent(event: E)
    }

    interface Event<E> {
        val type: String
        val data: E
    }

    val busses: MutableMap<String, MutableList<EventListener<Event<*>>>> = mutableMapOf()

    val byId: MutableMap<Int, EventListener<Event<*>>> = mutableMapOf()

    var latestUuid: Int = 0

    fun <E : Any?> on(event: String, func: Event<E>.(E) -> Unit) {
        val listener = object : EventListener<Event<E>> {
            override val uuid: Int = latestUuid++
            override fun onEvent(event: Event<E>) = event.func(event.data)
        } as EventListener<Event<*>>
        if (busses.containsKey(event)) {
            busses[event]!!.add(listener)
        } else {
            busses[event] = mutableListOf(listener)
        }
        byId[listener.uuid] = listener
    }

    fun <A : Any?> emit(event: String, data: A) {
        if (!busses.containsKey(event)) return // TODO: Default ("UNKNOWN" event) event bus & "ALL" event
        CoroutineScope((Dispatchers.Default)).launch {
            val listeners = busses[event]!!
            val iterator = listeners.iterator()
            while (iterator.hasNext()) {
                val listener = iterator.next()


                withContext(Dispatchers.Default) {
                    listener.onEvent(object : Event<A> {
                        override val type: String = event
                        override val data: A = data
                    })
                }

            }
        }
    }
    fun <A : Any?> directEmit(event: String, data: A) {
        if (!busses.containsKey(event)) return // TODO: Default ("UNKNOWN" event) event bus & "ALL" event
        val listeners = busses[event]!!
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            val listener = iterator.next()
            listener.onEvent(object : Event<A> {
                override val type: String = event
                override val data: A = data
            })
        }
    }

    fun <E : Any?> once(event: String, func: Event<E>.(E) -> Unit) {
        val listener = object : EventListener<Event<E>> {
            override val uuid: Int = latestUuid++
            override fun onEvent(event: Event<E>) {
                event.func(event.data)
                off(uuid)
            }
        } as EventListener<Event<*>>
        if (busses.containsKey(event)) {
            busses[event]!!.add(listener)
        } else {
            busses[event] = mutableListOf(listener)
        }
        byId[listener.uuid] = listener
    }

    suspend fun <E : Any?> waitFor(event: String): E {
        var result: E? = null
        once(event) {
            result = data
        }
        while (result == null) {
            delay(1)
        }
        return result!!
    }

    fun off(uuid: Int) {
        val listener = byId[uuid] ?: return
        busses.values.indexOfFirst { it.remove(listener) }
        byId.remove(uuid)
    }


}