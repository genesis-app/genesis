package uninit.common.compose.preferences

import platform.Foundation.NSUserDefaults

actual class PreferencesManager : PreferenceApi() {
    @Suppress("NAME_SHADOWING")
    override fun preference(key: String, defaultValue: String): Preference<String> {
        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.stringForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setObject(value, key)
        })
    }

    @Suppress("NAME_SHADOWING", "USELESS_ELVIS")
    override fun preference(key: String, defaultValue: Int): Preference<Int> {

        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.integerForKey(key).toInt() ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
        })
    }

    @Suppress("NAME_SHADOWING", "USELESS_ELVIS")
    override fun preference(key: String, defaultValue: Long): Preference<Long> {

        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.integerForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setInteger(value, key)
        })

    }

    @Suppress("NAME_SHADOWING", "USELESS_ELVIS")
    override fun preference(key: String, defaultValue: Float): Preference<Float> {
        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.floatForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setFloat(value, key)
        })
    }

    // elvis operator always returns
    @Suppress("NAME_SHADOWING", "USELESS_ELVIS")
    override fun preference(key: String, defaultValue: Boolean): Preference<Boolean> {
        return Preference(key, defaultValue, { key, defaultValue ->
            NSUserDefaults.standardUserDefaults.boolForKey(key) ?: defaultValue
        }, { value ->
            NSUserDefaults.standardUserDefaults.setBool(value, key)
        })
    }

    @Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
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