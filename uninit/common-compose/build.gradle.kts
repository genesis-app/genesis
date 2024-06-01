plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    `maven-publish`
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    jvm("desktop")
    iosArm64().binaries.framework {
        baseName = "uninitCommonCompose"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.serialization.json)
                implementation(libs.kotlinx.coroutines.core)

                api(libs.compose.runtime)
                api(libs.compose.foundation)
                api(libs.compose.material3)

                api(libs.ktor.client.core)
                api(libs.ktor.client.negotiation)

                api(libs.coil)
                api(libs.coil.svg)
                api(libs.coil.compose)
                api(libs.coil.network.core)

                // These have to be implementation or else gradle will pitch a fit
                api(libs.koin.core)
                api(libs.koin.compose)

                api(project(":uninit:common"))

                api(libs.compose.thirdparty.webview)

            }
            resources.srcDirs("resources")
        }

        val androidMain by getting {}
        val desktopMain by getting {
            dependencies {
                implementation(libs.jvm.gson)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "uninit.common.compose"

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
publishing {
    @Suppress("UNCHECKED_CAST")
    (extra["maven-repository"] as (PublishingExtension.() -> Unit)?)?.invoke(this)

    publications {
        create<MavenPublication>("uninit.common.compose") {
            groupId = "uninit"
            artifactId = "common-compose"
            version = project.version.toString()
            from(components["kotlin"])
        }
    }
}