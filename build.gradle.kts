plugins {
    kotlin("multiplatform") version "2.2.10"
}

group = "io.github.anstyle"
version = "0.1.0-SNAPSHOT"

kotlin {
    applyDefaultHierarchyTemplate()

    // Native targets
    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("commonMain/anstyle/src")
            kotlin.srcDir("commonMain/anstyle-parse/src")
            kotlin.srcDir("commonMain/anstyle-lossy/src")
        }

        val commonTest by getting {
            kotlin.srcDir("commonMain/anstyle/tests")
            kotlin.srcDir("commonMain/anstyle-parse/tests")
            kotlin.srcDir("commonMain/anstyle-lossy/tests")
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nativeMain by getting {
            // No external dependencies - anstyle-kotlin is a pure Kotlin library
        }

        val nativeTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
