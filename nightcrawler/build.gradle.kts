plugins {
    id(libs.plugins.kotlin.kapt.get().pluginId)
    alias(libs.plugins.kotlin.plugin.allopen)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
}

application {
    mainClass.set("money.tegro.market.nightcrawler.Application")
}

micronaut {
    version("3.5.1")
    processing {
        incremental(true)
        annotations("money.tegro.market.nightcrawler.*")
    }
}

dependencies {
    kapt(libs.micronaut.data.processor)
    kapt(libs.micronaut.http.validation)

    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.data.r2dbc)
    implementation(libs.micronaut.flyway)
    implementation(libs.micronaut.kotlin.extensions)
    implementation(libs.micronaut.kotlin.runtime)
    implementation(libs.micronaut.picocli)
    implementation(libs.micronaut.reactor)
    implementation(libs.micronaut.reactor.http.client)
    implementation(libs.jakarta.annotation)
    implementation(libs.reflect)
    implementation(libs.picocli)
    implementation(libs.logging)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.reactor)

    runtimeOnly(libs.r2dbc.pool)
    runtimeOnly(libs.r2dbc.postgresql)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.logback.core)
    runtimeOnly(libs.logback.classic)
    implementation(libs.logstash.logback.encoder)

    implementation(libs.micronaut.validation)
    implementation(libs.ton)

    runtimeOnly(libs.jackson)

    implementation(projects.blockchain)
    implementation(projects.core)
}
