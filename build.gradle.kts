@file:Suppress("UNUSED_VARIABLE")

import org.gradle.kotlin.dsl.accessors.tasks.PrintAccessors
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    `build-scan`
    application
    kotlin("jvm") version Versions.kotlin
    id("com.github.ben-manes.versions").version("0.17.0")
//    kotlin("kapt") version "1.2.21"
}

application {
    mainClassName = "samples.HelloCoroutinesKt"
}

kotlin { // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}


repositories {
    jcenter()
    mavenCentral()
    google()
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://dl.bintray.com/jerady/maven") }
    maven { setUrl("https://dl.bintray.com/kotlin/ktor") }
    maven { setUrl("https://dl.bintray.com/kotlin/exposed") }
}


// missing in the kotlin-dsl
fun DependencyHandler.`compile`(deps: List<String>) {
    for (dep in deps) add("compile", dep)
}


dependencies {
    testCompile(Deps.KotlinTest)
    testCompile(Deps.JUnit)
    testCompile(Deps.MockitoKotlin)
    testCompile(Deps.MockitoCore)
    testCompile(kotlin("test"))
    testCompile(kotlin("test-junit"))
    testCompile(Deps.Koin("koin-test").first())

    testCompileOnly(Deps.Jsr305)
    compileOnly(Deps.Jsr305)
    compile(Deps.Koin("koin-core"))

    // kotlin
    compile(kotlin("stdlib-jre8"))
    compile(kotlin("reflect"))

    // concurrency
    compile(Deps.Coroutines("kotlinx-coroutines-core", "kotlinx-coroutines-rx2"))
    compile(Deps.RxJava2)
    compile(Deps.RxKotlin)


    // html
    compile(Deps.JTwig)
    compile(Deps.KotlinXHtml)
    compile(Deps.Selenium)

    // UX
    compile(Deps.TornadoFx)
    compile(Deps.ControlsFx)
    compile(Deps.FontAwesomeFx)


    // IO
    compile(Deps.Okio)
    compile(Deps.Moshi)
    compile(Deps.MoshiLazyAdapters)
    compile(listOf(Deps.Okhttp, Deps.OkhttpLogging, Deps.OkhttpMockserver))
    compile(Deps.Retrofit("retrofit", "converter-moshi", "adapter-rxjava", "retrofit-mock", "adapter-rxjava2"))
    compile(Deps.RetrofitCoroutines)
    compile(Deps.ZeroTurnAround)
    compile(Deps.Sl4J)
    compile(Deps.Timber)
    compile(Deps.JDom)
    compile(Deps.Konfig)
    compile(Deps.Sl4J)
    compile(Deps.Timber)

    // Data
    compile(Deps.JodaTime)
    compile(Deps.Krangl)
    compile(Deps.KotlinPoet)
    compile(Deps.Docopt)
    compile(Deps.Expose)
    compile(Deps.Postgres)


}

buildScan {
    setLicenseAgreementUrl("https://gradle.com/terms-of-service")
    setLicenseAgree("yes")
}


/**
 * JSR305 nullability annotations
 *
 * See https://medium.com/square-corner-blog/non-null-is-the-default-58ffc0bb9111
 * See https://github.com/Kotlin/KEEP/blob/jsr-305/proposals/jsr-305-custom-nullability-qualifiers.md
 * See https://github.com/square/tape/commit/8d87c7de3c799261f387019b793ee08bcce43545
 * See https://stackoverflow.com/a/11807961/936870
 * Many thanks to Eric Cochran /  Gabriel Ittner / Benoît Quenaudon
 * **/
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

val hello1 = tasks.newSimpleTask(name = "hello1", description = "Prints Hello World") {
    println("Hello World 1")
}
val hello2 = tasks.newSimpleTask(name = "hello2", dependsOn = listOf(":hello1")) {
    println("Hello World 2")
}
val hello3 = tasks.newSimpleTask(name = "hello3", dependsOnTasks = listOf(hello1, hello2)) {
    println("Hello World 3")
    println("Hello World 3")
}


