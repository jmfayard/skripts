pluginManagement {
    //if (System.getenv("USE_LOCALLY_PUBLISHED_PLUGIN") != null) {
        repositories {
            maven { url = uri("/Users/jmfayard/akelius/buildSrcVersions/plugin/build/repository") }
            gradlePluginPortal()
        }
    //}
}
rootProject.name = "skripts"
