plugins {
    kotlin("multiplatform") version "2.2.10"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.kotlinmania"
version = "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    applyDefaultHierarchyTemplate()

    // Native targets
    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.kotlinmania:roff-kotlin:0.1.0")
                implementation("io.github.kotlinmania:cansi-kotlin:0.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
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
