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
            version("coroutines", "1.6.0")
            version("json", "1.3.3")
            version("cli", "0.3.4")
            version("ipfs", "1.3.3")
            version("ton", "attempt_fix_tlbytesdecoding-SNAPSHOT")

            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef("coroutines")
            library("json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("json")
            library("cli", "org.jetbrains.kotlinx", "kotlinx-cli").versionRef("cli")
            library("ipfs", "com.github.ipfs", "java-ipfs-http-client").versionRef("ipfs")
            library("ton", "com.github.antonmeep.ton-kotlin", "ton-kotlin").versionRef("ton")
        }
    }
}

include(":nft_tool")
