package uninit.common.fytix

sealed class Result<T, E> {
    fun <R> map(transform: (T) -> R): Result<R, E> {
        return when (this) {
            is Ok -> Ok(transform(value))
            is Err -> Err(error)
        }
    }

    fun <R> mapError(transform: (E) -> R): Result<T, R> {
        return when (this) {
            is Ok -> Ok(value)
            is Err -> Err(transform(error))
        }
    }

    fun isOk(): Boolean {
        return when (this) {
            is Ok -> true
            is Err -> false
        }
    }

    fun isError(): Boolean {
        return when (this) {
            is Ok -> false
            is Err -> true
        }
    }

    fun getOrNull(): T? {
        return when (this) {
            is Ok -> value
            is Err -> null
        }
    }

    fun errorOrNull(): E? {
        return when (this) {
            is Ok -> null
            is Err -> error
        }
    }

    fun unwrap(): T {
        return when (this) {
            is Ok -> value
            is Err -> throw Exception("Result is Err")
        }
    }

    fun unwrapError(): E {
        return when (this) {
            is Ok -> throw Exception("Result is Ok")
            is Err -> error
        }
    }
}
class Ok<T, E>(val value: T) : Result<T, E>()
class Err<T, E>(val error: E) : Result<T, E>()

fun <T> result(block: () -> T): Result<T, Exception> {
    return try {
        Ok(block())
    } catch (e: Exception) {
        Err(e)
    }
}


