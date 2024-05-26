package uninit.common.compose.theme

import uninit.common.compose.theme.catppuccin.*

sealed class ThemeRegistry {
    private val map: MutableMap<String, ApplicationTheme> = mutableMapOf()

    /**
     * Registers a theme with the registry.
     *
     * @param theme The theme to register
     */
    fun register(theme: ApplicationTheme) {
        map[theme.id] = theme
    }

    /**
     * Gets a theme by its ID.
     *
     * @param id The ID of the theme
     * @return The theme
     */
    fun get(id: String): ApplicationTheme? {
        return map[id]
    }

    /**
     * Gets a theme by its ID, or returns the default theme if the theme is not found.
     *
     * @param id The ID of the theme
     * @return The theme, or the default theme if the theme is not found
     */
    fun getOrDefault(id: String): ApplicationTheme {
        return map[id] ?: ApplicationTheme.default
    }

    /**
     * Filters the themes to only those that are in the specified "folder".
     *
     * Example:
     * ```kt
     * val registry = ThemeRegistry()
     * registry.insertBuiltins()
     * val mocha = registry.filterToFolder("Catppuccin/Mocha")
     * // Only themes with IDs starting with "Catppuccin/Mocha" will be returned
     * // For example, "Catppuccin/Mocha/Lavender" and "Catppuccin/Mocha/Blue".
     * // Something like "Catppuccin/Mocha1/Lavender" will not be returned.
     * ```
     *
     * @param path The path to filter to
     * @return The filtered themes
     */
    fun filterToFolder(path: String): Map<String, ApplicationTheme> {
        return map.filter {
            it.key == path || it.key.startsWith("$path/")
        }
    }

    /**
     * Inserts the built-in themes into the registry.
     */
    fun insertBuiltins() {
        this.map.apply {
            for (theme in listOf(
                CatppuccinLatteLavender,
                CatppuccinLatteBlue,
                CatppuccinLatteSapphire,
                CatppuccinLatteSky,
                CatppuccinLatteTeal,
                CatppuccinLatteGreen,
                CatppuccinLatteYellow,
                CatppuccinLattePeach,
                CatppuccinLatteMaroon,
                CatppuccinLatteRed,
                CatppuccinLatteMauve,
                CatppuccinLattePink,
                CatppuccinLatteFlamingo,
                CatppuccinLatteRosewater,
                CatppuccinFrappeLavender,
                CatppuccinFrappeBlue,
                CatppuccinFrappeSapphire,
                CatppuccinFrappeSky,
                CatppuccinFrappeTeal,
                CatppuccinFrappeGreen,
                CatppuccinFrappeYellow,
                CatppuccinFrappePeach,
                CatppuccinFrappeMaroon,
                CatppuccinFrappeRed,
                CatppuccinFrappeMauve,
                CatppuccinFrappePink,
                CatppuccinFrappeFlamingo,
                CatppuccinFrappeRosewater,
                CatppuccinMacchiatoLavender,
                CatppuccinMacchiatoBlue,
                CatppuccinMacchiatoSapphire,
                CatppuccinMacchiatoSky,
                CatppuccinMacchiatoTeal,
                CatppuccinMacchiatoGreen,
                CatppuccinMacchiatoYellow,
                CatppuccinMacchiatoPeach,
                CatppuccinMacchiatoMaroon,
                CatppuccinMacchiatoRed,
                CatppuccinMacchiatoMauve,
                CatppuccinMacchiatoPink,
                CatppuccinMacchiatoFlamingo,
                CatppuccinMacchiatoRosewater,
                CatppuccinMochaLavender,
                CatppuccinMochaBlue,
                CatppuccinMochaSapphire,
                CatppuccinMochaSky,
                CatppuccinMochaTeal,
                CatppuccinMochaGreen,
                CatppuccinMochaYellow,
                CatppuccinMochaPeach,
                CatppuccinMochaMaroon,
                CatppuccinMochaRed,
                CatppuccinMochaMauve,
                CatppuccinMochaPink,
                CatppuccinMochaFlamingo,
                CatppuccinMochaRosewater
            )) put(theme.id, theme)
        }
    }

    /**
     * Singleton instance of the theme registry.
     */
    companion object : ThemeRegistry() {
        init {
            insertBuiltins()
        }
    }
}

expect fun ThemeRegistry.appendPlatformBuiltins()