package uninit.ui.application

import uninit.ui.theme.ThemeRegistry

class ApplicationConfigurationScope {
    /**
     * List of modules to be loaded by Koin.
     */
    var koinModules = mutableListOf<Any>() // FIXME

    /**
     * Whether to download the webview modules on
     * desktop platforms. Requires
     */
    var useWebview = false

    /**
     * The theme to use for the application.
     *
     * Will default to the theme with the ID that
     * matches the preference [themePreferenceName] in
     * the PreferencesManager.
     */
    var themeId: String? = null

    /**
     * The name of the preference to use for the theme ID.

     * Defaults to "currentThemeId".
     */
    var themePreferenceName = "currentThemeId"

    /**
     * The theme registry to use for the application.
     *
     * Defaults to the singleton instance of ThemeRegistry.
     */
    var themeRegistry: ThemeRegistry = ThemeRegistry

    /**
     * The default theme ID to use if no theme ID is set
     * and no theme ID is found in the preferences.
     *
     * This will also set the theme ID in the preferences
     * if no theme ID is set.
     *
     * Defaults to "Catppuccin/Mocha/Pink".
     */
    var defaultThemeId: String = "Catppuccin/Mocha/Pink"


    /**
     * The UI kit to use for the application.
     *
     * Will default to the ui kit with the ID that
     * matches the preference [uikitPreferenceName] in
     * the PreferencesManager.
     */
    var uikitId: String? = null

    /**
     * The name of the preference to use for the UI kit ID.
     *
     * Defaults to "currentUIKitId".
     */

    var uikitPreferenceName = "currentUIKitId"

    /**
     * The default UI kit ID to use if no UI kit ID is set
     * and no UI kit ID is found in the preferences.
     *
     * This will also set the UI kit ID in the preferences
     * if no UI kit ID is set.
     *
     * Defaults to "builtin/uninit".
     */

    var defaultUIKitId = "builtin/uninit"

    /**
     * The name of the application.
     */
    var applicationName: String? = null

    /**
     * The version of the application.
     */
    var applicationVersion: String? = null

    /**
     * Whether the application's name is normalized
     * to lowercase dash-delimited on linux when
     * creating directories and files.
     *
     * This setting only applies to linux, and
     * only effects ASCII application names
     *
     * Example: "My App" -> "my-app"
     */
    var normalizeApplicationName = true

    /**
     * Which directory to use for the application's
     * cache and data storage on Windows.
     *
     * Use the `%APP_NAME%` placeholder to insert the
     * application name into the path.
     *
     * Defaults to the user's Local AppData directory.
     */
    var windowsDataDirectory: String? = null

    /**
     * Which directory to use for the application's
     * cache and data storage on macOS.
     *
     * Use the `%APP_NAME%` placeholder to insert the
     * application name into the path.
     *
     * Defaults to the user's Application Support directory.
     */
    var macDataDirectory: String? = null

    /**
     * Which directory to use for the application's
     * cache and data storage on Linux.
     *
     *
     * Use the `%APP_NAME%` placeholder to insert the
     * application name into the path.
     *
     * Defaults to "~/.%APP_NAME%".
     */
    var linuxDataDirectory: String? = null

}