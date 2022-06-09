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
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("clikt", "3.4.2")
            version("datetime", "0.3.3")
            version("exposed", "0.38.2")
            version("ipfs", "0.15")
            version("kodein", "7.11.0")
            version("logback", "1.2.11")
            version("logging", "2.1.23")
            version("reaktive", "1.2.1")
            version("reaktive.coroutines", "1.2.1-nmtc")
            version("serialization", "1.3.2")
            version("slf4j", "1.7.36")
            version("sqlite", "3.36.0")
            version("ton", "597de41105")

            library("clikt", "com.github.ajalt.clikt", "clikt").versionRef("clikt")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version {
                strictly("1.6.1-native-mt")
            }
            library("datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").versionRef("datetime")
            library("exposed.core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            library("exposed.dao", "org.jetbrains.exposed", "exposed-dao").versionRef("exposed")
            library("exposed.jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")
            library("exposed.kotlin.datetime", "org.jetbrains.exposed", "exposed-kotlin-datetime").versionRef("exposed")
            library("ipfs", "com.github.ligi", "ipfs-api-kotlin").versionRef("ipfs")
            library("kodein", "org.kodein.di", "kodein-di").versionRef("kodein")
            library("kodein.conf", "org.kodein.di", "kodein-di-conf").versionRef("kodein")
            library("logback", "ch.qos.logback", "logback-classic").versionRef("logback")
            library("logging", "io.github.microutils", "kotlin-logging").versionRef("logging")
            library("reaktive", "com.badoo.reaktive", "reaktive").versionRef("reaktive")
            library("reaktive.annotations", "com.badoo.reaktive", "reaktive-annotations").versionRef("reaktive")
            library("reaktive.coroutines", "com.badoo.reaktive", "coroutines-interop").versionRef("reaktive.coroutines")
            library(
                "serialization.core",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-core"
            ).versionRef("serialization")
            library(
                "serialization.json",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-json"
            ).versionRef("serialization")
            library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("sqlite", "org.xerial", "sqlite-jdbc").versionRef("sqlite")
            library("ton", "com.github.andreypfau.ton-kotlin", "ton-kotlin").versionRef("ton")
        }
    }
}

include(":common")
include(":nightcrawler")
include(":tool")
