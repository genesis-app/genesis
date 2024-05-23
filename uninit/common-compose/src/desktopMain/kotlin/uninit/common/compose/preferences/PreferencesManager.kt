package uninit.common.compose.preferences

import uninit.common.serialization.GsonAdapter

actual class PreferencesManager(prefsFile: String) : PreferenceApi() {
    var prefs: GsonAdapter
    init {
        prefs = GsonAdapter(prefsFile)
    }
    override fun preference(key: String, defaultValue: String): Preference<String> {
        var value by prefs.value(key, defaultValue)
        return Preference(key, defaultValue, { _, _ ->
            value
        }, { v ->
            value = v
        })
    }
    override fun preference(key: String, defaultValue: Int): Preference<Int> {
        var value by prefs.value(key, defaultValue)
        return Preference(key, defaultValue, {_, _ ->
            value
        }, { v ->
            value = v
        })
    }
    override fun preference(key: String, defaultValue: Long): Preference<Long> {
        var value by prefs.value(key, defaultValue)
        return Preference(key, defaultValue, { _, _ ->
            value
        }, { v ->
            value = v
        })
    }
    override fun preference(key: String, defaultValue: Float): Preference<Float> {
        var value by prefs.value(key, defaultValue)
        return Preference(key, defaultValue, { _, _ ->
            value
        }, { v ->
            value = v
        })
    }
    override fun preference(key: String, defaultValue: Boolean): Preference<Boolean> {
        var value by prefs.value(key, defaultValue)
        return Preference(key, defaultValue, { _, _ ->
            value
        }, { v ->
            value = v
        })
    }
    override fun preference(key: String, defaultValue: Set<String>): Preference<Set<String>> {
        var value by prefs.value(key, defaultValue)
        return Preference(key, defaultValue, { _, _ ->
            value
        }, { v ->
            value = v
        })
    }
}