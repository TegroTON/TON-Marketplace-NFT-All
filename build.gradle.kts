plugins {
    kotlin("multiplatform")
}

allprojects {
    group = "money.tegro"
    version = "1.0-SNAPSHOT"

    apply(plugin = "kotlin-multiplatform")

    // Build everything in the same directory
    project.buildDir = rootProject.buildDir

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    kotlin {
        jvm {
            withJava()
            compilations.all {
                kotlinOptions.jvmTarget = "11"
            }
        }
    }
}
