package uninit.common.compose.preferences

import kotlin.reflect.KClass

abstract class SerializablePreferenceApi : PreferenceApi() {
    abstract fun <T : Any> preference(
        key: String,
        defaultValue: T,
        klass: KClass<T>,
    ): Preference<T>
}