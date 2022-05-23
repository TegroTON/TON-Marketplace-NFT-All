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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("clikt", "3.4.2")
            version("coroutines", "1.6.0")
            version("ipfs", "1.3.3")
            version("json", "1.3.3")
            version("koin", "3.2.0")
            version("logback", "1.2.11")
            version("logging", "2.1.23")
//            version("serialization", "1.6.10")
            version("slf4j", "1.7.36")
            version("ton", "ce058f49fe")

            library("clikt", "com.github.ajalt.clikt", "clikt").versionRef("clikt")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8").versionRef("coroutines")
            library("ipfs", "com.github.ipfs", "java-ipfs-http-client").versionRef("ipfs")
            library("json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("json")
            library("koin", "io.insert-koin", "koin-core").versionRef("koin")
            library("logback", "ch.qos.logback", "logback-classic").versionRef("logback")
            library("logging", "io.github.microutils", "kotlin-logging").versionRef("logging")
            library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("ton", "com.github.andreypfau.ton-kotlin", "ton-kotlin").versionRef("ton")
//            library(
//                "serialization-core",
//                "org.jetbrains.kotlinx",
//                "kotlinx-serialization-core"
//            ).versionRef("serialization")
//            library(
//                "serialization-json",
//                "org.jetbrains.kotlinx",
//                "kotlinx-serialization-json"
//            ).versionRef("serialization")
        }
    }
}

include(":nft")
include(":nft_tool")
