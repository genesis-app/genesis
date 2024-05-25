package uninit.common.collections

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlin.jvm.JvmInline

@JvmInline value class Three<T>( internal val it: Array<T> ) : Collection<T> {
    override val size: Int
        get() = 3
    init {
        require(it.size == 3)
    }
    val first: T get() = it[0]
    val second: T get() = it[1]
    val third: T get() = it[2]
    override fun toString(): String = "Three($first, $second, $third)"
    operator fun component1(): T = first
    operator fun component2(): T = second
    operator fun component3(): T = third
    operator fun get(index: Int): T = it[index]
    override fun contains(element: T): Boolean {
        return first == element || second == element || third == element
    }
    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }
    override fun isEmpty(): Boolean = false
    override fun iterator(): Iterator<T> = it.iterator()
}

val <T> Array<T>.three: Three<T>
    get() = Three(this)