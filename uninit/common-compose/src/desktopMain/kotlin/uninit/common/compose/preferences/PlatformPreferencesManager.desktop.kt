package uninit.common.compose.preferences

import uninit.common.Platform
import uninit.common.platform.OsFamily
import uninit.common.serialization.GsonAdapter
import kotlin.reflect.KClass

/**
 * PlatformPreferencesManager is a multiplatform (Android, iOS, Desktop) preferences manager
 * that aims to provide a simple, consistent API for storing and retrieving preferences
 * of many types, including serializable objects.
 *
 * On desktop, this is implemented using a JSON file in a platform-specific config folder.
 * This file also supports object nesting (a key at `a.b.c` will be stored in the JSON
 * as `{"a": {"b": {"c": "value"}}}`).
 *
 * On Windows, this is `%APPDATA%/Genesis/genesis.json`
 *
 * On Linux and macOS, this is `~/.config/genesis/genesis.json`
 *
 * On Android, this is implemented using SharedPreferences. Serializable objects are
 * stored as JSON strings.
 *
 * On iOS, this is implemented using NSUserDefaults. Serializable objects are stored
 * as JSON strings.
 *
 *
 * ### Serializable data storage
 *
 * Serialized data, when taken as a (key: String, value: T) pair, is stored as a string value
 * in the platform preferences. setter and getter functions are required to provide reified
 * type information for serialization and deserialization as well as runtime type checking.
 *
 * For serialized data types, the key will be stored as "key#${T::class.simpleName}" and the
 * value will be stored as a JSON string.
 * @see [Preference]
 */
actual class PlatformPreferencesManager(prefsFile: String = platformPrefsFile()) : SerializablePreferenceApi() {
    var prefs: GsonAdapter
    init {
        prefs = GsonAdapter(prefsFile)
    }

    override fun preference(key: String, defaultValue: String): Preference<String>
        = prefs.value(key, defaultValue).toPreference(key, defaultValue)
    override fun preference(key: String, defaultValue: Int): Preference<Int>
        = prefs.value(key, defaultValue).toPreference(key, defaultValue)
    override fun preference(key: String, defaultValue: Long): Preference<Long>
        = prefs.value(key, defaultValue).toPreference(key, defaultValue)
    override fun preference(key: String, defaultValue: Float): Preference<Float>
        = prefs.value(key, defaultValue).toPreference(key, defaultValue)
    override fun preference(key: String, defaultValue: Boolean): Preference<Boolean>
        = prefs.value(key, defaultValue).toPreference(key, defaultValue)
    override fun preference(key: String, defaultValue: Set<String>): Preference<Set<String>>
        = prefs.value(key, defaultValue).toPreference(key, defaultValue)
    override fun <T : Any> preference(key: String, defaultValue: T, klass: KClass<T>): Preference<T>
        = prefs.value("${key}#[${klass.simpleName}]", defaultValue, klass).toPreference(key, defaultValue)
    companion object {
        private fun platformPrefsFile(): String {
            return when (Platform.osFamily) {
                OsFamily.WINDOWS -> System.getenv("APPDATA") + "/Genesis/genesis.json"
                OsFamily.LINUX -> System.getProperty("user.home") + "/.config/genesis/genesis.json"
                OsFamily.MACOS -> System.getProperty("user.home") + "/Library/Application Support/Genesis/genesis.json"
                else -> throw Exception("Unsupported platform")
            }
        }
    }
}