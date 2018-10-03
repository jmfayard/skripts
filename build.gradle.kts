@file:Suppress("UNUSED_VARIABLE")

import org.gradle.kotlin.dsl.accessors.tasks.PrintAccessors
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    `build-scan`
    application
    kotlin("jvm") version Versions.kotlin

    // https://plugins.gradle.org/plugin/jmfayard.github.io.gradle-kotlin-dsl-libs
    id("jmfayard.github.io.gradle-kotlin-dsl-libs").version("0.2.0")
//    id("com.github.ben-manes.versions").version("0.20.0")
//    kotlin("kapt") version "1.2.21"
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
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://dl.bintray.com/jerady/maven") }
    maven { setUrl("https://dl.bintray.com/kotlin/ktor") }
    maven { setUrl("https://dl.bintray.com/kotlin/exposed") }
}



dependencies {
    testCompile(Libs.kotlin_test)
    testCompile(Libs.junit)
    testCompile(Libs.mockito_kotlin)
    testCompile(Libs.mockito_core)
    testCompile(Libs.kotlintest)
    testCompile(Libs.kotlin_test_junit)

    testCompileOnly(Libs.jsr305)
    compileOnly(Libs.jsr305)

    compile(Libs.koin_core)

    // kotlin
    compile(Libs.kotlin_stdlib_jdk8)
    compile(Libs.kotlin_reflect)

    // concurrency
    compile(Libs.kotlinx_coroutines_core)
    compile(Libs.kotlinx_coroutines_rx2)
    compile(Libs.rxjava)
    compile(Libs.rxkotlin)


    // html
    compile(Libs.jtwig_core)
    compile(Libs.kotlinx_html_jvm)
    compile(Libs.selenium_java)

    // UX
    compile(Libs.tornadofx)
    compile(Libs.controlsfx)
    compile(Libs.fontawesomefx)


    // IO
    compile(Libs.okio)
    compile(Libs.moshi)
    compile(Libs.moshi_lazy_adapters)
    compile(Libs.okhttp)
    compile(Libs.logging_interceptor)
    compile(Libs.mockwebserver)
    compile(Libs.retrofit)
    compile(Libs.converter_moshi)
    compile(Libs.adapter_rxjava2)
    compile(Libs.retrofit_mock)
    compile(Libs.kotlin_coroutines_retrofit)

    compile(Libs.zt_exec)
    compile(Libs.slf4j_simple)
    compile(Libs.timber)
    compile(Libs.jdom)
    compile(Libs.konfig)

    // Data
    compile(Libs.joda_time)
    compile(Libs.krangl)
    compile(Libs.kotlinpoet)
    compile(Libs.docopt)
    compile(Libs.exposed)
    compile(Libs.postgresql)


}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
    publishAlways()
}


tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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
