package uninit.common.fytix

sealed class Option<T> {
    fun <R> map(transform: (T) -> R): Option<R> {
        return when (this) {
            is Some -> Some(transform(value))
            is None -> None()
        }
    }

    fun isSome(): Boolean {
        return when (this) {
            is Some -> true
            is None -> false
        }
    }

    fun isNone(): Boolean {
        return when (this) {
            is Some -> false
            is None -> true
        }
    }

    fun getOrNull(): T? {
        return when (this) {
            is Some -> value
            is None -> null
        }
    }

    fun unwrap(): T {
        return when (this) {
            is Some -> value
            is None -> throw Exception("Option is None")
        }
    }
}

class Some<T>(val value: T) : Option<T>()
class None<T> : Option<T>()

fun <T> option(block: () -> T?): Option<T> {
    return try {
        when (val result = block()) {
            null -> None()
            else -> Some(result)
        }
    } catch (e: Exception) {
        None()
    }
}