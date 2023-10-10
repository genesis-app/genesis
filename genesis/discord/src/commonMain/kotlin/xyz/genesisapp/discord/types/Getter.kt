package xyz.genesisapp.discord.types

class Getter<T>(val get: () -> T) {
    operator fun getValue(thisRef: Any?, property: Any?): T {
        return get()
    }

}
fun <T> getter(get: () -> T): Getter<T> = Getter(get)