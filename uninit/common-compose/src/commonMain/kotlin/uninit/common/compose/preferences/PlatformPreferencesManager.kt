package uninit.common.compose.preferences


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
 * For serialized data types, the key will be stored as "key#[${T::class.simpleName}]" and the
 * value will be stored as a JSON string.
 * @see [Preference]
 */
expect class PlatformPreferencesManager: SerializablePreferenceApi