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
                implementation(libs.ton)
                implementation(libs.coroutines)
                implementation(libs.cli)
            }
        }
    }
}
