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
            version("ton", "781dd09140")

            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef("coroutines")
            library("ton", "com.github.andreypfau.ton-kotlin", "ton-kotlin").versionRef("ton")
        }
    }
}

include(":tools:nft-item")
