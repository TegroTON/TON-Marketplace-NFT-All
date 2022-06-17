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
    implementation(coreLibs.reflect)
    implementation(coreLibs.ton)
    implementation(dbLibs.h2)
    implementation(dbLibs.hibernate.core)
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)
    implementation(logLibs.slf4j.simple)
    implementation(springLibs.core)
    implementation(springLibs.jdbc)
    implementation(springLibs.jpa)
    implementation(springLibs.openapi.ui)
    implementation(springLibs.openapi.kotlin)
    implementation(springLibs.web)

    implementation(projects.common)
}
