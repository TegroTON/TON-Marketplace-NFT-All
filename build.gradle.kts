plugins {
    kotlin("multiplatform")
}

allprojects {
    group = "money.tegro"
    version = "1.0-SNAPSHOT"

    apply(plugin = "kotlin-multiplatform")

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
