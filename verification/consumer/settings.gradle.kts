pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { maven { url = uri(providers.gradleProperty("producerRepository").get()) } }
            filter {
                listOf("", "-android", "-jvm").forEach { suffix ->
                    includeModule("io.github.kotlinmania", "anstyle-kotlin" + suffix)
                }
            }
        }
        google()
        mavenCentral()
    }
}
rootProject.name = "anstyle-public-api-consumer"
