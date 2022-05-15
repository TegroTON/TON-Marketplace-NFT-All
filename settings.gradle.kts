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
        //  TODO: version catalog for org.ton
        create("core") {
            library("coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
        }
    }
}

include(":tools:nft-item")
