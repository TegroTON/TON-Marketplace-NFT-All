plugins {
    id(libs.plugins.kotlin.kapt.get().pluginId)
    alias(libs.plugins.kotlin.plugin.allopen)
    alias(libs.plugins.micronaut.library)
}

micronaut {
    version("3.5.1")
    processing {
        incremental(true)
        annotations("money.tegro.market.core.*")
    }
}

dependencies {
    kapt(libs.micronaut.data.processor)
    kapt(libs.micronaut.http.validation)

    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.http.server)
    implementation(libs.micronaut.jackson.databind)
    implementation(libs.micronaut.data.r2dbc)
    implementation(libs.micronaut.kotlin.extensions)
    implementation(libs.micronaut.kotlin.runtime)
    implementation(libs.micronaut.picocli)
    implementation(libs.micronaut.reactor)
    implementation(libs.micronaut.reactor.http.client)
    implementation(libs.jakarta.annotation)
    implementation(libs.swagger.annotations)
    implementation(libs.reflect)
    implementation(libs.logging)

    implementation(libs.micronaut.validation)
    implementation(libs.ton)

    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.reactor)

    implementation(projects.blockchain)
}
