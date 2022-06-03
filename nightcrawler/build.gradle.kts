plugins {
    application
}

application {
    mainClass.set("money.tegro.market.nightcrawler.MainKt")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.clikt)
                implementation(libs.coroutines)
                implementation(libs.datetime)
                implementation(libs.exposed.core)
                implementation(libs.exposed.dao)
                implementation(libs.exposed.jdbc)
                implementation(libs.exposed.kotlin.datetime)
                implementation(libs.ipfs)
                implementation(libs.kodein)
                implementation(libs.kodein.conf)
                implementation(libs.logback)
                implementation(libs.logging)
                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
                implementation(libs.slf4j)
                implementation(libs.sqlite)
                implementation(libs.ton)
                implementation(projects.db)
                implementation(projects.nft)
            }
        }
    }
}
