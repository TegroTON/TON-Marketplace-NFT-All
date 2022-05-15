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
                // TODO: use version catalogs and proper versioning here
                implementation("org.ton:ton-adnl") {
                    version {
                        branch = "main"
                    }
                }
                implementation("org.ton:ton-bitstring") {
                    version {
                        branch = "main"
                    }
                }
                implementation("org.ton:ton-crypto") {
                    version {
                        branch = "main"
                    }
                }
                implementation("org.ton:ton-cell") {
                    version {
                        branch = "main"
                    }
                }
                implementation("org.ton:ton-tlb") {
                    version {
                        branch = "main"
                    }
                }
                implementation("org.ton:ton-lite-api") {
                    version {
                        branch = "main"
                    }
                }
                implementation(core.coroutines)
            }
        }
    }
}
