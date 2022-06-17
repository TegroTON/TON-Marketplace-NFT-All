plugins {
    application
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

application {
    mainClass.set("money.tegro.market.nightcrawler.ApplicationKt")
}

dependencies {
    implementation(coreLibs.clikt)
    implementation(coreLibs.coroutines)
    implementation(coreLibs.coroutines.reactive)
    implementation(coreLibs.coroutines.reactor)
    implementation(coreLibs.jackson)
    implementation(coreLibs.ton)
    implementation(dbLibs.h2)
    implementation(dbLibs.hibernate.core)
    implementation(logLibs.logging)
    implementation(logLibs.slf4j)
    implementation(logLibs.slf4j.simple)
    implementation(springLibs.core)
    implementation(springLibs.jdbc)
    implementation(springLibs.jpa)
    implementation(springLibs.batch.core)
    implementation(springLibs.batch.integration)

    implementation(projects.common)
}
