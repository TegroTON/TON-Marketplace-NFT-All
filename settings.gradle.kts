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

sourceControl {
    // TODO: proper maven repository instead of git
    gitRepository(java.net.URI("https://github.com/andreypfau/ton-kotlin.git")) {
        producesModule("org.ton:ton-adnl")
        producesModule("org.ton:ton-bitstring")
        producesModule("org.ton:ton-crypto")
        producesModule("org.ton:ton-cell")
        producesModule("org.ton:ton-tlb")
        producesModule("org.ton:ton-lite-api")
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
