package uninit.common.color.pallete

import uninit.common.color.RGBA
import kotlin.jvm.JvmInline

/**
 * Catppuccin
 */
sealed class Catppuccin(
    val id: String,
    val crust: RGBA,
    val mantle: RGBA,
    val base: RGBA,
    val surface0: RGBA,
    val surface1: RGBA,
    val surface2: RGBA,
    val overlay0: RGBA,
    val overlay1: RGBA,
    val overlay2: RGBA,
    val subtext0: RGBA,
    val subtext1: RGBA,
    val text: RGBA,
    val lavender: RGBA,
    val blue: RGBA,
    val sapphire: RGBA,
    val sky: RGBA,
    val teal: RGBA,
    val green: RGBA,
    val yellow: RGBA,
    val peach: RGBA,
    val maroon: RGBA,
    val red: RGBA,
    val mauve: RGBA,
    val pink: RGBA,
    val flamingo: RGBA,
    val rosewater: RGBA,
) {
    /**
     * The lightest Catppuccin theme, harmoniously inverting the essence of Catppuccin's dark themes.
     *
     */
    object Latte : Catppuccin(
        id = "Latte",
        crust = h("#dce0e8"),
        mantle = h("#e6e9ef"),
        base = h("#eff1f5"),
        surface0 = h("#ccd0da"),
        surface1 = h("#bcc0cc"),
        surface2 = h("#acb0be"),
        overlay0 = h("#9ca0b0"),
        overlay1 = h("#8c8fa1"),
        overlay2 = h("#7c7f93"),
        subtext0 = h("#6c6f85"),
        subtext1 = h("#5c5f77"),
        text = h("#4c4f69"),
        lavender = h("#7287fd"),
        blue = h("#1e66f5"),
        sapphire = h("#209fb5"),
        sky = h("#04a5e5"),
        teal = h("#179299"),
        green = h("#40a02b"),
        yellow = h("#df8e1d"),
        peach = h("#fe640b"),
        maroon = h("#e64553"),
        red = h("#d20f39"),
        mauve = h("#8839ef"),
        pink = h("#ea76cb"),
        flamingo = h("#dd7878"),
        rosewater = h("#dc8a78"),
    )

    /**
     * A less vibrant alternative using subdued colors for a muted aesthetic
     */
    object Frappe : Catppuccin(
        id = "Frappe",
        crust = h("#232634"),
        mantle = h("#292c3c"),
        base = h("#303446"),
        surface0 = h("#414559"),
        surface1 = h("#51576d"),
        surface2 = h("#626880"),
        overlay0 = h("#737994"),
        overlay1 = h("#838ba7"),
        overlay2 = h("#949cbb"),
        subtext0 = h("#a5adce"),
        subtext1 = h("#b5bfe2"),
        text = h("#c6d0f5"),
        lavender = h("#babbf1"),
        blue = h("#8caaee"),
        sapphire = h("#85c1dc"),
        sky = h("#99d1db"),
        teal = h("#81c8be"),
        green = h("#a6d189"),
        yellow = h("#e5c890"),
        peach = h("#ef9f76"),
        maroon = h("#ea999c"),
        red = h("#e78284"),
        mauve = h("#ca9ee6"),
        pink = h("#f4b8e4"),
        flamingo = h("#eebebe"),
        rosewater = h("#f2d5cf"),
    )

    /**
     * Medium contrast with gentle colors creating a soothing atmosphere
     */
    object Macchiato : Catppuccin(
        id = "Macchiato",
        crust = h("#181926"),
        mantle = h("#1e2030"),
        base = h("#24273a"),
        surface0 = h("#363a4f"),
        surface1 = h("#494d64"),
        surface2 = h("#5b6078"),
        overlay0 = h("#6e738d"),
        overlay1 = h("#8087a2"),
        overlay2 = h("#939ab7"),
        subtext0 = h("#a5adcb"),
        subtext1 = h("#b8c0e0"),
        text = h("#cad3f5"),
        lavender = h("#b7bdf8"),
        blue = h("#8aadf4"),
        sapphire = h("#7dc4e4"),
        sky = h("#91d7e3"),
        teal = h("#8bd5ca"),
        green = h("#a6da95"),
        yellow = h("#eed49f"),
        peach = h("#f5a97f"),
        maroon = h("#ee99a0"),
        red = h("#ed8796"),
        mauve = h("#c6a0f6"),
        pink = h("#f5bde6"),
        flamingo = h("#f0c6c6"),
        rosewater = h("#f4dbd6"),
    )

    /**
     * The Original - A vibrant and colorful theme with high contrast, offering
     * a cozy feeling with color-rich accents
     */
    object Mocha : Catppuccin(
        id = "Mocha",
        crust = h("#11111b"),
        mantle = h("#181825"),
        base = h("#1e1e2e"),
        surface0 = h("#313244"),
        surface1 = h("#45475a"),
        surface2 = h("#585b70"),
        overlay0 = h("#6c7086"),
        overlay1 = h("#7f849c"),
        overlay2 = h("#9399b2"),
        subtext0 = h("#a6adc8"),
        subtext1 = h("#bac2de"),
        text = h("#cdd6f4"),
        lavender = h("#b4befe"),
        blue = h("#89b4fa"),
        sapphire = h("#74c7ec"),
        sky = h("#89dceb"),
        teal = h("#94e2d5"),
        green = h("#a6e3a1"),
        yellow = h("#f9e2af"),
        peach = h("#fab387"),
        maroon = h("#eba0ac"),
        red = h("#f38ba8"),
        mauve = h("#cba6f7"),
        pink = h("#f5c2e7"),
        flamingo = h("#f2cdcd"),
        rosewater = h("#f5e0dc"),
    )
}
internal inline fun h(string: String): RGBA = RGBA.hex(string)