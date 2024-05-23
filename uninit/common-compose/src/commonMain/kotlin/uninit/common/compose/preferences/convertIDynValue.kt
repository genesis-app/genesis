package uninit.common.compose.preferences

import uninit.common.interfaces.IDynValue

fun <T> IDynValue<T>.toPreference(key: String, defaultValue: T): Preference<T> {
    var t by this
    return Preference(key, defaultValue, { _, _ ->
        return@Preference t
    }, { v ->
        t = v
    })
}