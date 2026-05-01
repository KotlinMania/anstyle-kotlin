import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    kotlin("multiplatform") version "2.3.20"
    id("com.android.kotlin.multiplatform.library") version "9.2.0"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.kotlinmania"
version = "0.1.4"

val androidSdkDir: String? =
    providers.environmentVariable("ANDROID_SDK_ROOT").orNull
        ?: providers.environmentVariable("ANDROID_HOME").orNull

if (androidSdkDir != null && file(androidSdkDir).exists()) {
    val localProperties = rootProject.file("local.properties")
    if (!localProperties.exists()) {
        val sdkDirPropertyValue = file(androidSdkDir).absolutePath.replace("\\", "/")
        localProperties.writeText("sdk.dir=$sdkDirPropertyValue")
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    compilerOptions {
        allWarningsAsErrors.set(true)
    }

    val xcf = XCFramework("Anstyle")

    macosArm64 {
        binaries.framework {
            baseName = "Anstyle"
            xcf.add(this)
        }
    }
    macosX64 {
        binaries.framework {
            baseName = "Anstyle"
            xcf.add(this)
        }
    }
    linuxX64()
    mingwX64()
    iosArm64 {
        binaries.framework {
            baseName = "Anstyle"
            xcf.add(this)
        }
    }
    iosX64 {
        binaries.framework {
            baseName = "Anstyle"
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "Anstyle"
            xcf.add(this)
        }
    }
    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.kotlinmania:roff-kotlin:0.1.4")
                implementation("io.github.kotlinmania:cansi-kotlin:0.1.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }

    jvmToolchain(21)
}

kotlin {
    androidLibrary {
        namespace = "io.github.kotlinmania.anstyle"
        compileSdk = 34
        minSdk = 24
    }
}

val enableIosSimulatorTests =
    providers.gradleProperty("enableIosSimulatorTests").map { it.toBoolean() }.orElse(false)

tasks.withType<KotlinNativeTest>().configureEach {
    if (!enableIosSimulatorTests.get() && (name == "iosX64Test" || name == "iosSimulatorArm64Test")) {
        enabled = false
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(group.toString(), "anstyle-kotlin", version.toString())

    pom {
        name.set("anstyle-kotlin")
        description.set("Kotlin Multiplatform library for ANSI text styling and terminal color support")
        inceptionYear.set("2024")
        url.set("https://github.com/KotlinMania/anstyle-kotlin")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("sydneyrenee")
                name.set("Sydney Renee")
                email.set("sydney@solace.ofharmony.ai")
                url.set("https://github.com/sydneyrenee")
            }
        }

        scm {
            url.set("https://github.com/KotlinMania/anstyle-kotlin")
            connection.set("scm:git:git://github.com/KotlinMania/anstyle-kotlin.git")
            developerConnection.set("scm:git:ssh://github.com/KotlinMania/anstyle-kotlin.git")
        }
    }
}
