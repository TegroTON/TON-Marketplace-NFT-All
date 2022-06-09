plugins {
    application
    id("com.github.johnrengelman.shadow")
    id("org.springframework.boot")
    id("org.springdoc.openapi-gradle-plugin")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

application {
    mainClass.set("money.tegro.market.drive.ApplicationKt")
}

dependencies {
    implementation(coreLibs.clikt)
    implementation(coreLibs.coroutines)
    implementation(coreLibs.jackson)
    implementation(coreLibs.reaktive)
    implementation(coreLibs.reaktive.annotations)
    implementation(coreLibs.reaktive.coroutines)
    implementation(coreLibs.reflect)
    implementation(coreLibs.ton)
    implementation(dbLibs.exposed.core)
    implementation(dbLibs.exposed.dao)
    implementation(dbLibs.exposed.java.time)
    implementation(logLibs.logback)
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)
    implementation(webLibs.springdoc.openapi.ui)
    implementation(webLibs.springdoc.openapi.kotlin)
    implementation(webLibs.spring.boot.web)

    implementation(projects.common)
}
