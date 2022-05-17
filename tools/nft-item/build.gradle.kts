plugins {
    application
}
application {
    mainClass.set("money.tegro.market.tools.nft.item.MainKt")
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
