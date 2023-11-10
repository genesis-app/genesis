plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    androidTarget()

    jvm("desktop")

    iosArm64().binaries.framework {
        baseName = "genesisCommon"
//            binaryOption("bundleId", "xyz.genesisapp.common")
//            isStatic = true
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.serialization.json)
                compileOnly(compose.runtime)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

            }

            resources.srcDirs("resources")
        }
        val androidMain by getting {
            dependencies {

            }
        }
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)

            languageSettings {

            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.jvm.gson)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "xyz.genesisapp.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
