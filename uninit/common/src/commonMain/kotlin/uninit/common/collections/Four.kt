package uninit.common.collections

import kotlin.jvm.JvmInline

//open class Four<T, U, V, W>(
//    val first: T,
//    val second: U,
//    val third: V,
//    val fourth: W,
//) {
//    override fun toString(): String = "Four($first, $second, $third, $fourth)"
//}


@JvmInline value class Four<T> ( internal val it: Array<T> ) : Collection<T> {
    override val size: Int
        get() = 4
    init {
        require(it.size == 4)
    }
    val first: T get() = it[0]
    val second: T get() = it[1]
    val third: T get() = it[2]
    val fourth: T get() = it[3]

    override fun toString(): String = "Four($first, $second, $third, $fourth)"
    operator fun component1(): T = first
    operator fun component2(): T = second
    operator fun component3(): T = third
    operator fun component4(): T = fourth
    operator fun get(index: Int): T = it[index]
    override fun contains(element: T): Boolean {
        return first == element || second == element || third == element || fourth == element
    }
    override fun containsAll(elements: Collection<T>): Boolean {
        return elements.all { contains(it) }
    }
    override fun isEmpty(): Boolean = false
    override fun iterator(): Iterator<T> = it.iterator()
}

val <T> Array<T>.four: Four<T>
    get() = Four(this)