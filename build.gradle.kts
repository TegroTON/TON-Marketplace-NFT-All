plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    kotlin("plugin.spring") version "1.7.10"

    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
}

group = "money.tegro"
version = "0.5.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

kotlin {
    jvm("spring") {
        withJava()

        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
    }
    js("react", IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                devServer?.port = 8081
                cssSupport.enabled = true
                outputFileName = "index.js"
                outputPath = File(buildDir, "processedResources/spring/main/static")
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:atomicfu:1.6.21")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

                implementation("io.github.microutils:kotlin-logging:2.1.23")
            }
        }

        val reactMain by getting {
            dependencies {
                dependsOn(commonMain)

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
            dependencies {
                dependsOn(commonMain)

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
                implementation("org.postgresql:r2dbc-postgresql")
                compileOnly("org.springframework.boot:spring-boot-configuration-processor")
            }
        }
    }
}

tasks.getByName<Copy>("springProcessResources") {
    from(tasks.getByName("reactBrowserDistribution")) {
        into("static")
    }
}
