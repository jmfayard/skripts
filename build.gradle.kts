@file:Suppress("UNUSED_VARIABLE")

import org.gradle.kotlin.dsl.accessors.tasks.PrintAccessors
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `build-scan`
    application
    kotlin("jvm") version "1.2.61"
    id("jmfayard.github.io.gradle-kotlin-dsl-libs") version "0.2.6"
}

application {
    mainClassName = "samples.HelloCoroutinesKt"
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}


repositories {
    mavenCentral()
    jcenter()
    google()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/jerady/maven")
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/exposed")
}



dependencies {
    testImplementation(Libs.kotlin_test)
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito_kotlin)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.kotlintest)
    testImplementation(Libs.kotlin_test_junit)

    testCompileOnly(Libs.jsr305)
    compileOnly(Libs.jsr305)

    implementation(Libs.koin_core)

    // kotlin
    implementation(Libs.kotlin_stdlib_jdk8)
    implementation(Libs.kotlin_reflect)

    // concurrency
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_rx2)
    implementation(Libs.rxjava)
    implementation(Libs.rxkotlin)


    // html
    implementation(Libs.jtwig_core)
    implementation(Libs.kotlinx_html_jvm)
    implementation(Libs.selenium_java)

    // UX
    implementation(Libs.tornadofx)
    implementation(Libs.controlsfx)
    implementation(Libs.fontawesomefx)


    // IO
    implementation(Libs.okio)
    implementation(Libs.moshi)
    implementation(Libs.moshi_lazy_adapters)
    implementation(Libs.okhttp)
    implementation(Libs.logging_interceptor)
    implementation(Libs.mockwebserver)
    implementation(Libs.retrofit)
    implementation(Libs.converter_moshi)
    implementation(Libs.adapter_rxjava2)
    implementation(Libs.retrofit_mock)
    implementation(Libs.kotlin_coroutines_retrofit)

    implementation(Libs.zt_exec)
    implementation(Libs.slf4j_simple)
    implementation(Libs.timber)
    implementation(Libs.jdom)
    implementation(Libs.konfig)

    // Data
    implementation(Libs.joda_time)
    implementation(Libs.krangl)
    implementation(Libs.kotlinpoet)
    implementation(Libs.docopt)
    implementation(Libs.exposed)
    implementation(Libs.postgresql)

    implementation(Libs.kodein_di_generic_jvm)
    implementation("com.github.holgerbrandl:kscript-support:1.2.4")


}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
    publishAlways()
}



tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}



java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceSets {
        val main: SourceSet by getting
        val test: SourceSet by getting
        main.java.setSrcDirs(listOf("kotlin"))
        test.java.srcDir("test/kotlin")
        test.resources.srcDir("test/resources")
    }
}


val copyToLib by tasks.creating(Copy::class) {
    from(configurations.runtime)
    into("lib")
}
