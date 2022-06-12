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
    implementation(dbLibs.h2)
    implementation(dbLibs.hibernate.core)
    implementation(logLibs.logback)
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)
    implementation(springLibs.core)
    implementation(springLibs.jdbc)
    implementation(springLibs.jpa)
    implementation(springLibs.openapi.ui)
    implementation(springLibs.openapi.kotlin)
    implementation(springLibs.web)

    implementation(projects.common)
}
