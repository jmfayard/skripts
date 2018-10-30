pluginManagement {
    if (System.getenv("USE_LOCALLY_PUBLISHED_PLUGIN") != null) {
        repositories {
            maven { url = uri("/Users/jmfayard/Dev/try/gradle-kotlin-dsl-libs/build/repository") }
            maven { url = uri("https://plugins.gradle.org/m2/") }
        }
    }
}
rootProject.name = "skripts"