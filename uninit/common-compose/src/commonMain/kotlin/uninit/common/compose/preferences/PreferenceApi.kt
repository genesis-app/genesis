package uninit.common.compose.preferences

abstract class PreferenceApi {

    abstract fun preference(
        key: String,
        defaultValue: String,
    ): Preference<String>

    abstract fun preference(
        key: String,
        defaultValue: Int,
    ): Preference<Int>

    abstract fun preference(
        key: String,
        defaultValue: Long,
    ): Preference<Long>

    abstract fun preference(
        key: String,
        defaultValue: Float,
    ): Preference<Float>

    abstract fun preference(
        key: String,
        defaultValue: Boolean,
    ): Preference<Boolean>

    abstract fun preference(
        key: String,
        defaultValue: Set<String>,
    ): Preference<Set<String>>
}