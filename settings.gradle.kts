pluginManagement {
    repositories {
        val localGradleRepo = "/Users/jmfayard/akelius/buildSrcVersions/plugin/build/repository"
        if (File(localGradleRepo).exists()) maven { url = uri(localGradleRepo) }
        gradlePluginPortal()
    }
}
rootProject.name = "skripts"
