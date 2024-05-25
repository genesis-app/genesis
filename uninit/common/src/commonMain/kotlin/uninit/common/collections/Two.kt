package uninit.common.collections

import kotlin.jvm.JvmInline

@JvmInline value class Two<T>(internal val it: Array<T>) : Collection<T> {
    override val size: Int
        get() = 2
    init {
        require(it.size == 2)
    }
    val first: T get() = it[0]
    val second: T get() = it[1]
    override fun toString(): String = "Two($first, $second)"
    operator fun component1(): T = first
    operator fun component2(): T = second
    operator fun get(index: Int): T = it[index]
    override fun contains(element: T): Boolean {
        return first == element || second == element
    }
    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }
    override fun isEmpty(): Boolean = false
    override fun iterator(): Iterator<T> = it.iterator()

}

val <T> Array<T>.two: Two<T>
    get() = Two(this)