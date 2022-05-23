plugins {
    application
}
application {
    mainClass.set("money.tegro.market.nft_tool.MainKt")
}
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.clikt)
                implementation(libs.coroutines)
                implementation(libs.ipfs)
                implementation(libs.json)
                implementation(libs.koin)
                implementation(libs.logback)
                implementation(libs.logging)
                implementation(libs.slf4j)
                implementation(libs.ton)
                implementation(projects.nft)
            }
        }
    }
}
