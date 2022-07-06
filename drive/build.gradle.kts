plugins {
    id(libs.plugins.kotlin.kapt.get().pluginId)
    alias(libs.plugins.kotlin.plugin.allopen)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
}

application {
    mainClass.set("money.tegro.market.drive.Application")
}

micronaut {
    version("3.5.1")
    processing {
        incremental(true)
        annotations("money.tegro.market.drive.*")
    }
}

dependencies {
    kapt(libs.micronaut.data.processor)
    kapt(libs.picocli.codegen)
    kapt(libs.micronaut.http.validation)
    kapt(libs.micronaut.openapi)

    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.http.server.netty)
    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.data.r2dbc)
    implementation(libs.micronaut.kotlin.extensions)
    implementation(libs.micronaut.kotlin.runtime)
    implementation(libs.micronaut.reactor)
    implementation(libs.micronaut.reactor.http.client)
    implementation(libs.micronaut.security)
    implementation(libs.jakarta.annotation)
    implementation(libs.swagger.annotations)
    implementation(libs.reflect)

    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.reactor)

    runtimeOnly(libs.r2dbc.h2)
    runtimeOnly(libs.slf4j)
    runtimeOnly(libs.logback.core)
    runtimeOnly(libs.logback.classic)

    implementation(libs.micronaut.validation)
    implementation(libs.ton)

    implementation(libs.jackson)

    implementation(projects.core)
}
