/**
 * Find which updates are available by running
 *     `$ ./gradlew syncLibs`
 * This will only update the comments.
 *
 * YOU are responsible for updating manually the dependency version. */
object Versions {
    const val jsr305: String = "3.0.2" 

    const val com_gradle_build_scan_gradle_plugin: String = "1.16" //available: "2.0.1" 

    const val timber: String = "4.7.1" 

    const val konfig: String = "1.5.0.0" //available: "1.6.10.0" 

    const val mockito_kotlin: String = "1.6.0" 

    const val docopt: String = "0.6.0.20150202" 

    const val moshi_lazy_adapters: String = "2.2" 

    const val moshi: String = "1.7.0" 

    const val com_squareup_okhttp3: String = "3.11.0" 

    const val okio: String = "2.0.0" //available: "2.1.0" 

    const val com_squareup_retrofit2: String = "2.4.0" 

    const val kotlinpoet: String = "0.6.0" //available: "0.7.0" 

    const val fontawesomefx: String = "8.9" 

    const val krangl: String = "0.6" //available: "0.10.3" 

    const val kotlintest: String = "1.3.7" //available: "2.0.7" 

    const val rxjava: String = "2.2.2" //available: "2.2.3" 

    const val rxkotlin: String = "2.3.0" 

    const val jmfayard_github_io_gradle_kotlin_dsl_libs_gradle_plugin: String = "0.2.6" 

    const val joda_time: String = "2.10" //available: "2.10.1" 

    const val junit: String = "4.12" 

    const val tornadofx: String = "1.7.17" 

    const val controlsfx: String = "9.0.0" 

    const val jdom: String = "2.0.2" 

    const val exposed: String = "0.10.3" //available: "0.11.1" 

    const val org_jetbrains_kotlin_jvm_gradle_plugin: String = "1.2.61" //available: "1.3.0" 

    const val kotlin_reflect: String = "1.2.71" //available: "1.3.0" 

    const val kotlin_scripting_compiler_embeddable: String = "1.2.61" //available: "1.3.0" 

    const val kotlin_stdlib_jdk8: String = "1.2.71" //available: "1.3.0" 

    const val kotlin_test_junit: String = "1.2.71" //available: "1.3.0" 

    const val kotlin_test: String = "1.2.71" //available: "1.3.0" 

    const val kotlinx_coroutines_core: String = "0.21.1" //available: "1.0.0" 

    const val kotlinx_coroutines_rx2: String = "0.21.1" //available: "1.0.0" 

    const val kotlinx_html_jvm: String = "0.6.11" 

    const val jtwig_core: String = "5.87.0.RELEASE" 

    const val kodein_di_generic_jvm: String = "5.3.0" 

    const val koin_core: String = "0.9.1" //available: "1.0.1" 

    const val mockito_core: String = "2.23.0" 

    const val postgresql: String = "42.2.5" 

    const val selenium_java: String = "2.41.0" //available: "3.14.0" 

    const val slf4j_simple: String = "1.7.25" 

    const val zt_exec: String = "1.10" 

    const val kotlin_coroutines_retrofit: String = "0.9.0" //available: "0.13.0" 

    /**
     *
     *   To update Gradle, edit the wrapper file at path:
     *      ./gradle/wrapper/gradle-wrapper.properties
     */
    object Gradle {
        const val runningVersion: String = "4.10.2"

        const val currentVersion: String = "4.10.2"

        const val nightlyVersion: String = "5.1-20181030000041+0000"

        const val releaseCandidate: String = ""
    }
}
