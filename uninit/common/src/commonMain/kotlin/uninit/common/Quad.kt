package uninit.common

open class Quad<T, U, V, W>(
    val first: T,
    val second: U,
    val third: V,
    val fourth: W,
) {
    override fun toString(): String = "Quad($first, $second, $third, $fourth)"
}
