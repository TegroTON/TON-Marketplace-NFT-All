kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.ipfs)
                implementation(libs.json)
                implementation(libs.koin)
                implementation(libs.logback)
                implementation(libs.logging)
                implementation(libs.slf4j)
                implementation(libs.ton)
            }
        }
    }
}
