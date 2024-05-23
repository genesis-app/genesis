import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlin.compose.plugin)
}

kotlin {
    jvm()
    jvmToolchain(17)
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(when (getCurrentTarget()) {
                    "windows-x64" -> libs.compose.desktop.jvm.windows.x64
                    "macos-x64" -> libs.compose.desktop.jvm.macos.x64
                    "linux-x64" -> libs.compose.desktop.jvm.linux.x64
                    "linux-arm64" -> libs.compose.desktop.jvm.linux.arm64
                    "macos-arm64" -> libs.compose.desktop.jvm.macos.arm64
                    else -> throw Exception("Unsupported target")
                })
                implementation(project(":genesis:app"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "uninit.genesis.Application"

        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Dmg,
                TargetFormat.Exe
            )
            packageName = "Genesis"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("../genesis/app/src/commonMain/resources/icons")
            macOS {
                bundleID = "uninit.genesis"
                iconFile.set(iconsRoot.resolve("genesis.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("genesis.ico"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("genesis.png"))
            }
        }
    }
}

fun getCurrentTarget(): String {
    val osSys = System.getProperty("os.name")
    val OS = when {
        osSys.contains("win", ignoreCase = true) -> "windows"
        osSys.contains("mac", ignoreCase = true) -> "macos"
        osSys.contains("nix") || osSys.contains("nux") || osSys.contains("aix") -> "linux"
        else -> throw Exception("Unsupported OS")
    }
    val archSys = System.getProperty("os.arch")
    val arch = when (archSys) {
        "x86_64", "amd64" -> "x64"
        "aarch64" -> "arm64"
        else -> throw Exception("Unsupported arch")
    }
    return "$OS-$arch"
}
