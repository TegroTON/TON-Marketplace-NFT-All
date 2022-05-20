rootProject.name = "market"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    plugins {
        kotlin("multiplatform") version "1.6.21"
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("coroutines", "1.6.0")
            version("cli", "0.3.4")
            version("ton", "0c042c741d")

            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef("coroutines")
            library("cli", "org.jetbrains.kotlinx", "kotlinx-cli").versionRef("cli")
            library("ton", "com.github.andreypfau.ton-kotlin", "ton-kotlin").versionRef("ton")
        }
    }
}

include(":nft_tool")
