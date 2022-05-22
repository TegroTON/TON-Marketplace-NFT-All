rootProject.name = "market"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    plugins {
        kotlin("multiplatform") version "1.6.21"
        kotlin("plugin.serialization") version "1.6.10"
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("clikt", "3.4.2")
            version("coroutines", "1.6.0")
            version("ipfs", "1.3.3")
            version("json", "1.3.3")
            version("koin", "3.2.0")
            version("ton", "attempt_fix_tlbytesdecoding-SNAPSHOT")

            library("clikt", "com.github.ajalt.clikt", "clikt").versionRef("clikt")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef("coroutines")
            library("ipfs", "com.github.ipfs", "java-ipfs-http-client").versionRef("ipfs")
            library("json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("json")
            library("koin", "io.insert-koin", "koin-core").versionRef("koin")
            library("ton", "com.github.antonmeep.ton-kotlin", "ton-kotlin").versionRef("ton")
        }
    }
}

include(":nft_tool")
