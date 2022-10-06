import com.github.gradle.node.npm.task.NpxTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    kotlin("plugin.spring") version "1.7.0"
    id("com.github.node-gradle.node") version "3.4.0"
}

group = "money.tegro"
version = "0.4.1"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.github.microutils:kotlin-logging:2.1.23")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("com.github.andreypfau.ton-kotlin:ton-kotlin:c678f34b0a")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
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
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation(kotlin("script-runtime"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

node {
    version.set("16.17.1")
    download.set(true)
}

tasks.register<NpxTask>("buildTypeScript") {
    dependsOn("npmInstall")
    command.set("esbuild")
    args.addAll(
        "--bundle",
        "--minify",
        "--inject:esbuild.inject.js",
        "--outfile=./src/main/resources/static/bundle.js",
        "./web/ts/index.ts"
    )
    inputs.files("package.json", "package-lock.json", "tsconfig.json")
    inputs.dir("web/ts")
    inputs.dir(fileTree("node_modules").exclude(".cache"))
    outputs.file(file("src/main/resources/static/bundle.js"))
}

tasks.register<NpxTask>("buildTailwindCSS") {
    dependsOn("npmInstall")
    command.set("tailwindcss")
    args.addAll(
        "--minify",
        "--input=./web/css/index.css",
        "--output=./src/main/resources/static/bundle.css",
    )
    inputs.files("package.json", "package-lock.json", "tailwind.config.js")
    inputs.dir("web/css")
    inputs.dir("src/main/resources/templates")
    inputs.dir(fileTree("node_modules").exclude(".cache"))
    outputs.file(file("src/main/resources/templates/static/bundle.css"))
}


tasks.withType<ProcessResources> {
    dependsOn("buildTypeScript")
    dependsOn("buildTailwindCSS")
}
