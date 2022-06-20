import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
}

allprojects {
    group = "money.tegro"
    version = "0.0.1"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    if (project != rootProject)
        project.buildDir = File(rootProject.buildDir, project.name)

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
