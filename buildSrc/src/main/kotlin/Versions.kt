import kotlin.String
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Find which updates are available by running
 *     `$ ./gradlew buildSrcVersions`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version.
 */
object Versions {
    const val org_jlleitschuh_gradle_ktlint_gradle_plugin: String = "8.2.0"

    const val de_fayard_buildsrcversions_gradle_plugin: String = "0.5.0"

    const val org_jetbrains_kotlin_jvm_gradle_plugin: String = "1.3.41" // available: "1.3.50"

    const val com_gradle_build_scan_gradle_plugin: String = "2.3" // available: "2.4.2"

    const val kotlin_coroutines_retrofit: String = "1.1.0"

    const val kotlintest_runner_junit5: String = "3.4.0" // available: "3.4.1"

    const val kotlinx_coroutines_core: String = "1.2.2" // available: "1.3.1"

    const val com_squareup_retrofit2: String = "2.6.1"

    const val kotlinx_coroutines_rx2: String = "1.2.2" // available: "1.3.1"

    const val kodein_di_generic_jvm: String = "6.3.3"

    const val com_squareup_okhttp3: String = "4.0.1" // available: "4.2.0"

    const val org_jetbrains_kotlin: String = "1.3.41" // available: "1.3.50"

    const val moshi_lazy_adapters: String = "2.2"

    const val kotlinx_html_jvm: String = "0.6.12"

    const val kscript_support: String = "1.2.5"

    const val mockito_kotlin: String = "1.6.0"

    const val fontawesomefx: String = "8.9"

    const val selenium_java: String = "3.141.59"

    const val mockito_core: String = "3.0.0"

    const val slf4j_simple: String = "1.7.27" // available: "1.7.28"

    const val kotlinpoet: String = "1.3.0"

    const val controlsfx: String = "11.0.0"

    const val jtwig_core: String = "5.87.0.RELEASE"

    const val postgresql: String = "42.2.6"

    const val joda_time: String = "2.10.3"

    const val tornadofx: String = "1.7.19"

    const val config4k: String = "0.4.1"

    const val rxkotlin: String = "2.4.0"

    const val exposed: String = "0.16.3" // available: "0.17.3"

    const val zt_exec: String = "1.11"

    const val jsr305: String = "3.0.2"

    const val timber: String = "4.7.1"

    const val docopt: String = "0.6.0.20150202"

    const val ktlint: String = "0.33.0" // available: "0.34.2"

    const val config: String = "1.3.4"

    const val krangl: String = "0.11"

    const val rxjava: String = "2.2.11" // available: "2.2.12"

    const val moshi: String = "1.8.0"

    const val junit: String = "4.12"

    const val okio: String = "2.3.0"

    const val jdom: String = "2.0.2"

    /**
     *
     * See issue 19: How to update Gradle itself?
     * https://github.com/jmfayard/buildSrcVersions/issues/19
     */
    const val gradleLatestVersion: String = "5.6.2"

    const val gradleCurrentVersion: String = "5.5.1"
}

/**
 * See issue #47: how to update buildSrcVersions itself
 * https://github.com/jmfayard/buildSrcVersions/issues/47
 */
val PluginDependenciesSpec.buildSrcVersions: PluginDependencySpec
    inline get() =
            id("de.fayard.buildSrcVersions").version(Versions.de_fayard_buildsrcversions_gradle_plugin)
