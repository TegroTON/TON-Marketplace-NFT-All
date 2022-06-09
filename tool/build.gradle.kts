plugins {
    application
    id("com.github.johnrengelman.shadow")
}

application {
    mainClass.set("money.tegro.market.tool.MainKt")
}

dependencies {
    implementation(coreLibs.clikt)
    implementation(coreLibs.coroutines)
    implementation(coreLibs.jackson)
    implementation(coreLibs.ton)
    implementation(logLibs.logback)
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)

    implementation(projects.common)
}
