rootProject.name = "uninit-genesis"
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://repo.uninit.dev/snapshots")
        maven("https://repo.uninit.dev/releases")
        maven("https://repo.uninit.dev/local")

    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0")
}

include(":appAndroid")
include(":appDesktop")
//include(":appIos") this is an xcode proj, not gradle.
include(":genesis:app")
include(":genesis:discord:api")
include(":genesis:discord:client")
include(":genesis:common")
include(":genesis:genesisApi")
// include(":genesis:nativeVoice") TODO: soon:tm:

include(":uninit:common")
include(":uninit:common-compose")
