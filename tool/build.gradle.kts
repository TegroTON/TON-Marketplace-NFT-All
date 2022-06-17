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
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)
    implementation(logLibs.slf4j.simple)

    implementation(projects.common)
}
