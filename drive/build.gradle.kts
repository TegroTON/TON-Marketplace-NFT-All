plugins {
    id(libs.plugins.kotlin.kapt.get().pluginId)
    alias(libs.plugins.kotlin.plugin.allopen)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
}

application {
    mainClass.set("money.tegro.market.drive.ApplicationKt")
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

    implementation(libs.picocli)
    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.data.r2dbc)
    implementation(libs.micronaut.kotlin.extensions)
    implementation(libs.micronaut.kotlin.runtime)
    implementation(libs.micronaut.picocli)
    implementation(libs.micronaut.reactor)
    implementation(libs.micronaut.reactor.http.client)
    implementation(libs.jakarta.annotation)
    implementation(libs.reflect)

    runtimeOnly(libs.r2dbc.h2)
    runtimeOnly(libs.slf4j.simple)

    implementation(libs.micronaut.validation)
    implementation(libs.ton)

    runtimeOnly(libs.jackson)
}
