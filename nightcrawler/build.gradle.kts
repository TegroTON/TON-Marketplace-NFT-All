plugins {
    application
}
application {
    mainClass.set("money.tegro.market.nightcrawler.MainKt")
}
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.clikt)
                implementation(libs.coroutines)
                implementation(libs.exposed.core)
                implementation(libs.exposed.dao)
                implementation(libs.exposed.jdbc)
                implementation(libs.ipfs)
                implementation(libs.koin)
                implementation(libs.logback)
                implementation(libs.logging)
                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
                implementation(libs.slf4j)
                implementation(libs.sqlite)
                implementation(libs.ton)
                implementation(projects.nft)
            }
        }
    }
}
