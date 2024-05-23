plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.compose.compiler)
//    alias(libs.plugins.kotlin.compose.plugin)
//    id("dev.icerock.mobile.multiplatform-resources")
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget()

    jvm("desktop")

    iosArm64().binaries.framework {
        baseName = "genesisApp"
        binaryOption("bundleId", "uninit.genesis.app")
        isStatic = true
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.material)
                implementation(libs.compose.components.resources)

//                api(libs.moko.resources.common)
//                api(libs.moko.resources.compose)

                implementation(libs.serialization.json)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheetNavigator)
                implementation(libs.voyager.tabNavigator)
                implementation(libs.voyager.transitions)
                implementation(libs.voyager.core)
                implementation(libs.voyager.koin)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                implementation(libs.ktor.client.core)

                implementation(libs.napier)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

//                implementation(libs.kamel)


                implementation(project(":uninit:common"))
                implementation(project(":uninit:common-compose"))
                implementation(project(":genesis:discord:api"))
                implementation(project(":genesis:discord:client"))
                implementation(project(":genesis:genesisApi"))

            }

        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.8.0")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
                implementation(libs.ktor.client.okhttp)

                dependsOn(commonMain)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.compose.desktop.common)
                implementation(libs.ktor.client.okhttp)

                dependsOn(commonMain)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "uninit.genesis.app"

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
 