package uninit.common.compose.preferences

import android.content.SharedPreferences
import uninit.common.compose.preferences.Preference
import uninit.common.compose.preferences.PreferenceApi

@Suppress("NAME_SHADOWING")
actual class PreferencesManager(val prefs: SharedPreferences) : PreferenceApi() {
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
}