plugins {
    application
    id("com.github.johnrengelman.shadow")
}

application {
    mainClass.set("money.tegro.market.drive.ApplicationKt")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.datetime)
                implementation(libs.exposed.core)
                implementation(libs.exposed.dao)
                implementation(libs.exposed.jdbc)
                implementation(libs.exposed.kotlin.datetime)
                implementation(libs.kodein)
                implementation(libs.kodein.conf)
                implementation(libs.ktor.serialization.kotlinxJson)
                implementation(libs.ktor.server.cachingHeaders)
                implementation(libs.ktor.server.callId)
                implementation(libs.ktor.server.callLogging)
                implementation(libs.ktor.server.compression)
                implementation(libs.ktor.server.conditionalHeaders)
                implementation(libs.ktor.server.contentNegotiation)
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.hostCommon)
                implementation(libs.ktor.server.locations)
                implementation(libs.ktor.server.metrics)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.statusPages)
                implementation(libs.logback)
                implementation(libs.logging)
                implementation(libs.reaktive)
                implementation(libs.reaktive.annotations)
                implementation(libs.reaktive.coroutines)
                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
                implementation(libs.slf4j)
                implementation(libs.sqlite)
                implementation(libs.ton)
                implementation(projects.common)
            }
        }
    }
}
