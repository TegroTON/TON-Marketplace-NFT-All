plugins {
    application
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("money.tegro.market.tool.MainKt")
}

dependencies {
    implementation(libs.clikt)
    implementation(libs.bundles.coroutines)
    implementation(libs.jackson)
    implementation(libs.ton)
    implementation(libs.logging)
    implementation(libs.slf4j.simple)

    implementation(projects.blockchain)
}
