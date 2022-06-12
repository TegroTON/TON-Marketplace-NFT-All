rootProject.name = "market"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    plugins {
        kotlin("jvm") version "1.7.0"
        kotlin("plugin.allopen") version "1.7.0"
        kotlin("plugin.jpa") version "1.7.0"
        kotlin("plugin.noarg") version "1.7.0"
        kotlin("plugin.spring") version "1.7.0"
        id("org.springframework.boot") version "2.6.8"
        id("org.springdoc.openapi-gradle-plugin") version "1.3.4"
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("coreLibs") {
            version("clikt", "3.4.2")
            version("jackson", "2.13.2")
            version("reaktive", "1.2.1")
            version("reaktive.coroutines", "1.2.1-nmtc")
            version("reflect", "1.7.0")
            version("ton", "597de41105")

            library("clikt", "com.github.ajalt.clikt", "clikt").versionRef("clikt")
            library(
                "coroutines",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-core-jvm"
            ).version { strictly("1.6.1-native-mt") }
            library(
                "coroutines.reactive",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-reactive"
            ).version { strictly("1.6.1-native-mt") }
            library(
                "coroutines.reactor",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-reactor"
            ).version { strictly("1.6.1-native-mt") }
            library("jackson", "com.fasterxml.jackson.module", "jackson-module-kotlin").versionRef("jackson")
            library("reaktive", "com.badoo.reaktive", "reaktive").versionRef("reaktive")
            library("reaktive.annotations", "com.badoo.reaktive", "reaktive-annotations").versionRef("reaktive")
            library("reaktive.coroutines", "com.badoo.reaktive", "coroutines-interop").versionRef("reaktive.coroutines")
            library("reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("reflect")
            library("ton", "com.github.andreypfau.ton-kotlin", "ton-kotlin").versionRef("ton")
        }

        create("logLibs") {
            version("logback", "1.2.11")
            version("logging", "2.1.23")
            version("slf4j", "1.7.36")

            library("logback", "ch.qos.logback", "logback-classic").versionRef("logback")
            library("logging", "io.github.microutils", "kotlin-logging").versionRef("logging")
            library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")
        }

        create("dbLibs") {
            version("h2", "2.1.212")
            version("hibernate", "5.6.9.Final")

            library("h2", "com.h2database", "h2").versionRef("h2")
            library("hibernate.core", "org.hibernate", "hibernate-core").versionRef("hibernate")
        }

        create("springLibs") {
            version("springdoc.openapi", "1.6.9")
            version("spring.batch", "4.3.6")
            version("spring.boot", "2.6.8")

            library("openapi.ui", "org.springdoc", "springdoc-openapi-ui").versionRef("springdoc.openapi")
            library(
                "openapi.kotlin",
                "org.springdoc",
                "springdoc-openapi-kotlin"
            ).versionRef("springdoc.openapi")
            library("batch.core", "org.springframework.batch", "spring-batch-core").versionRef("spring.batch")
            library(
                "batch.integration",
                "org.springframework.batch",
                "spring-batch-integration"
            ).versionRef("spring.batch")

            library("core", "org.springframework.boot", "spring-boot-starter").versionRef("spring.boot")
            library("jdbc", "org.springframework.boot", "spring-boot-starter-jdbc").versionRef("spring.boot")
            library("jpa", "org.springframework.boot", "spring-boot-starter-data-jpa").versionRef("spring.boot")
            library(
                "security",
                "org.springframework.boot",
                "spring-boot-starter-security"
            ).versionRef("spring.boot")
            library("web", "org.springframework.boot", "spring-boot-starter-web").versionRef("spring.boot")
        }
    }
}

include(":common")
include(":drive")
include(":nightcrawler")
include(":tool")
