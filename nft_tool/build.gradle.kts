plugins {
    application
    kotlin("plugin.serialization")
}
application {
    mainClass.set("money.tegro.market.nft_tool.MainKt")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.json)
                implementation(libs.cli)
                implementation(libs.ipfs)
                implementation(libs.ton)
            }
        }
    }
}
