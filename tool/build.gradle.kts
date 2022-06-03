plugins {
    application
}

application {
    mainClass.set("money.tegro.market.tool.MainKt")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.clikt)
                implementation(libs.coroutines)
                implementation(libs.ipfs)
                implementation(libs.kodein)
                implementation(libs.kodein.conf)
                implementation(libs.logback)
                implementation(libs.logging)
                implementation(libs.serialization.core)
                implementation(libs.serialization.json)
                implementation(libs.slf4j)
                implementation(libs.ton)
                implementation(projects.common)
            }
        }
    }
}
