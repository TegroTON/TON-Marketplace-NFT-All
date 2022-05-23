plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

allprojects {
    group = "money.tegro"
    version = "1.0-SNAPSHOT"

    apply(plugin = "kotlin-multiplatform")
    apply(plugin = "kotlinx-serialization")

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
        sourceSets {
            val commonMain by getting {
                dependencies {
                    subprojects {
                        api(this)
                    }
                }
            }
            val commonTest by getting {
                dependencies {
//                    implementation(kotlin("test"))
                }
            }
        }
    }
}
