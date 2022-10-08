import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"

    id("org.springframework.boot") version "2.7.4" apply false
    id("io.spring.dependency-management") version "1.0.14.RELEASE" apply false

    kotlin("plugin.spring") version "1.7.10" apply false
}

group = "money.tegro"
version = "0.5.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

kotlin {
    jvm("spring") {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
        apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "17"
            }
        }
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
    js("react", IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                outputFileName = "index.js"
                outputPath = File(buildDir, "processedResources/spring/main/static")
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.microutils:kotlin-logging:2.1.23")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
            }
        }

        val reactMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.399")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.399")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.399")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:6.3.0-pre.399")

                implementation(npm("postcss", "^8.4.17"))
                implementation(npm("postcss-loader", "7.0.1"))
                implementation(npm("autoprefixer", "10.4.12"))
                implementation(npm("tailwindcss", "3.1.8"))
            }
        }
        val springMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("net.logstash.logback:logstash-logback-encoder:7.2")
                implementation("com.github.andreypfau.ton-kotlin:ton-kotlin:c678f34b0a")
                implementation("org.springframework.boot:spring-boot-starter-actuator")
                implementation("org.springframework.boot:spring-boot-starter-amqp")
                implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
                implementation("org.springframework.boot:spring-boot-starter-jdbc")
                implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
                implementation("org.springframework.boot:spring-boot-starter-webflux")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("com.github.ben-manes.caffeine:caffeine")
                implementation("com.sksamuel.aedile:aedile-core:1.0.2")
                implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
                implementation("org.flywaydb:flyway-core")
                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
                implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
                implementation("org.springframework.boot:spring-boot-devtools")
                runtimeOnly("io.micrometer:micrometer-registry-prometheus")
                runtimeOnly("org.postgresql:postgresql")
                runtimeOnly("org.postgresql:r2dbc-postgresql")
                compileOnly("org.springframework.boot:spring-boot-configuration-processor")
            }
        }
    }
}

tasks.getByName<Copy>("springProcessResources") {
    dependsOn(tasks.getByName("reactBrowserDevelopmentWebpack"))
}
