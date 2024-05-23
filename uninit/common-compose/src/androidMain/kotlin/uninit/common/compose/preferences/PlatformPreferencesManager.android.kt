package uninit.common.compose.preferences

import android.content.SharedPreferences
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import uninit.common.compose.preferences.Preference
import uninit.common.compose.preferences.SerializablePreferenceApi
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

@Suppress("NAME_SHADOWING")
actual class PlatformPreferencesManager(val prefs: SharedPreferences) : SerializablePreferenceApi() {
    override fun preference(key: String, defaultValue: String): Preference<String> {
        return Preference(key, defaultValue, { key, defaultValue ->
            prefs.getString(key, defaultValue) ?: defaultValue
        }, { value ->
            prefs.edit().putString(key, value).apply()
        })
    }

    override fun preference(key: String, defaultValue: Int): Preference<Int> {
        return Preference(key, defaultValue, { key, defaultValue ->
            prefs.getInt(key, defaultValue)
        }, { value ->
            prefs.edit().putInt(key, value).apply()
        })
    }

    override fun preference(key: String, defaultValue: Long): Preference<Long> {
        return Preference(key, defaultValue, { key, defaultValue ->
            prefs.getLong(key, defaultValue)
        }, { value ->
            prefs.edit().putLong(key, value).apply()
        })
    }

    override fun preference(key: String, defaultValue: Float): Preference<Float> {
        return Preference(key, defaultValue, { key, defaultValue ->
            prefs.getFloat(key, defaultValue)
        }, { value ->
            prefs.edit().putFloat(key, value).apply()
        })
    }

    override fun preference(key: String, defaultValue: Boolean): Preference<Boolean> {
        return Preference(key, defaultValue, { key, defaultValue ->
            prefs.getBoolean(key, defaultValue)
        }, { value ->
            prefs.edit().putBoolean(key, value).apply()
        })
    }

    override fun preference(key: String, defaultValue: Set<String>): Preference<Set<String>> {
        return Preference(key, defaultValue, { key, defaultValue ->
            prefs.getStringSet(key, defaultValue) ?: defaultValue
        }, { value ->
            prefs.edit().putStringSet(key, value).apply()
        })
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> preference(key: String, defaultValue: T, klass: KClass<T>): Preference<T> {
        return Preference("$key#[${klass.simpleName}]", defaultValue, { key, defaultValue ->
            val json = prefs.getString(key, null)
            if (json == null) {
                defaultValue
            } else {
                Json.decodeFromString(klass.serializer(), json)
            }
        }, { value ->
            prefs.edit().putString(key, Json.encodeToString(klass.serializer(), value)).apply()
        })
    }
}