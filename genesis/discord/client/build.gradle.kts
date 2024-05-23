plugins {
    kotlin("multiplatform")
    id("com.android.library")
//    id("org.jetbrains.compose")
    alias(libs.plugins.kotlinx.serialization)
    `maven-publish`
}

kotlin {
    androidTarget()

    jvm("desktop")

    iosArm64().binaries.framework {
        baseName = "genesisDiscordClient"
        binaryOption("bundleId", "uninit.genesis.discord.client")
        isStatic = true
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.serialization.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.negotiation)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.json)
                compileOnly(libs.compose.runtime)
                compileOnly(libs.compose.foundation)

//                compileOnly(libs.kamel)

                compileOnly(libs.koin.core)
                compileOnly(libs.koin.compose)

                implementation(libs.napier)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation(project(":genesis:discord:api"))
                implementation(project(":uninit:common"))
                implementation(project(":uninit:common-compose"))

            }

            resources.srcDirs("resources")
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "uninit.genesis.discord.client"

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

version = "0.0.1"

publishing {
    @Suppress("UNCHECKED_CAST")
    (extra["maven-repository"] as (PublishingExtension.() -> Unit)?)?.invoke(this)

    publications {
        create<MavenPublication>("uninit.genesis-discord-client") {
            groupId = "uninit"
            artifactId = "genesis-discord-client"
            version = project.version.toString()
            from(components["kotlin"])
        }
    }
}