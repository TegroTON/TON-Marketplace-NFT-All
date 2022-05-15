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
                // TODO: proper versions and all
                implementation("com.github.andreypfau.ton-kotlin:ton-kotlin:ca53e595fb")
                implementation(core.coroutines)
            }
        }
    }
}
