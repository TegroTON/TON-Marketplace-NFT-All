plugins {
    application
    id("com.github.johnrengelman.shadow")
}

application {
    mainClass.set("money.tegro.market.nightcrawler.MainKt")
}

dependencies {
    implementation(coreLibs.clikt)
    implementation(coreLibs.coroutines)
    implementation(coreLibs.jackson)
    implementation(coreLibs.reaktive)
    implementation(coreLibs.reaktive.annotations)
    implementation(coreLibs.reaktive.coroutines)
    implementation(coreLibs.kodein)
    implementation(coreLibs.kodein.conf)
    implementation(coreLibs.ton)
    implementation(dbLibs.exposed.core)
    implementation(dbLibs.exposed.dao)
    implementation(dbLibs.exposed.java.time)
    implementation(logLibs.logback)
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)

    implementation(projects.common)
}
