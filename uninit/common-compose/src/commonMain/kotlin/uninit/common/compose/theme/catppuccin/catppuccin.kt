package uninit.common.compose.theme.catppuccin

import uninit.common.collections.three
import uninit.common.collections.two
import uninit.common.color.pallete.Catppuccin
import uninit.common.compose.theme.ApplicationTheme
import uninit.common.compose.theme.color

internal fun catppuccin(flavor: String, variant: String? = null) = lazy {
    val flavor = when (flavor.lowercase()) {
        "latte" -> Catppuccin.Latte
        "frappe" -> Catppuccin.Frappe
        "macchiato" -> Catppuccin.Macchiato
        "mocha" -> Catppuccin.Mocha
        else -> throw IllegalArgumentException("Invalid flavor: $flavor")
    }
    val theVariant = when (variant?.lowercase()) {
        "lavender" -> flavor.lavender
        "blue" -> flavor.blue
        "sapphire" -> flavor.sapphire
        "sky" -> flavor.sky
        "teal" -> flavor.teal
        "green" -> flavor.green
        "yellow" -> flavor.yellow
        "peach" -> flavor.peach
        "maroon" -> flavor.maroon
        "red" -> flavor.red
        "mauve" -> flavor.mauve
        "pink" -> flavor.pink
        "flamingo" -> flavor.flamingo
        "rosewater" -> flavor.rosewater
        else -> flavor.pink // Default to pink :3
    }

    return@lazy ApplicationTheme(
        id = "Catppuccin/${flavor.id}/${(variant?.lowercase() ?: "pink").let { 
            "${it[0].uppercase()}${it.substring(1)}"
        }}",
        backgroundPane = flavor.base.color,
        secondaryPanes = arrayOf(
            flavor.crust.color,
            flavor.mantle.color,
        ).two,
        surfaceElements = arrayOf(
            flavor.surface0.color,
            flavor.surface1.color,
            flavor.surface2.color,
        ).three,
        overlays = arrayOf(
            flavor.overlay0.color,
            flavor.overlay1.color,
            flavor.overlay2.color,
        ).three,
        body = flavor.text.color,
        header = flavor.text.color,
        subheaders = arrayOf(
            flavor.subtext0.color,
            flavor.subtext1.color,
        ).two,
        accent = theVariant.color,
        success = flavor.green.color,
        warning = flavor.yellow.color,
        error = flavor.red.color,
        info = flavor.blue.color,
        cursor = flavor.rosewater.color,
    )
}