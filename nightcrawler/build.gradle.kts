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
    implementation(coreLibs.ton)
    implementation(dbLibs.exposed.core)
    implementation(dbLibs.exposed.dao)
    implementation(dbLibs.exposed.java.time)
    implementation(dbLibs.exposed.jdbc)
    implementation(dbLibs.sqlite)
    implementation(logLibs.logback)
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)

    implementation(projects.common)
}
