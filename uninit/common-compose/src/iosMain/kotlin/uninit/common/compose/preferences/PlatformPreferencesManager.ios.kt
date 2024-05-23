package uninit.common.compose.preferences

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import platform.Foundation.NSUserDefaults
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
@Suppress("UNCHECKED_CAST", "NAME_SHADOWING", "USELESS_ELVIS")
actual class PlatformPreferencesManager : SerializablePreferenceApi() {
    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> preference(key: String, defaultValue: T, klass: KClass<T>): Preference<T> {
        return Preference("$key#[${klass.simpleName}]", defaultValue, { key, defaultValue ->
            val value = NSUserDefaults.standardUserDefaults.stringForKey(key)
            when (value) {
                null -> defaultValue
                else -> Json.decodeFromString(klass.serializer(), value)
            }
        }, { value ->
            NSUserDefaults.standardUserDefaults.setObject(Json.encodeToString(klass.serializer(), value), key)
        })
    }

    override fun preference(key: String, defaultValue: String): Preference<String> {
        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.stringForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setObject(value, key)
        })
    }

    override fun preference(key: String, defaultValue: Int): Preference<Int> {

        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.integerForKey(key).toInt() ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
        })
    }

    override fun preference(key: String, defaultValue: Long): Preference<Long> {

        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.integerForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setInteger(value, key)
        })

    }

    override fun preference(key: String, defaultValue: Float): Preference<Float> {
        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.floatForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setFloat(value, key)
        })
    }

    // elvis operator always returns
    override fun preference(key: String, defaultValue: Boolean): Preference<Boolean> {
        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.boolForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setBool(value, key)
        })
    }

    override fun preference(key: String, defaultValue: Set<String>): Preference<Set<String>> {
        return Preference(key, defaultValue, { key, defaultValue ->
            val value =
                (NSUserDefaults.standardUserDefaults.stringArrayForKey(key) as List<String>).toSet()
            when (value.size) {
                0 -> defaultValue
                else -> value
            }
        }, { value ->
            NSUserDefaults.standardUserDefaults.setObject(value, key)
        })
    }
}