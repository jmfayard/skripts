import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    val kotlinVerion = "1.1.51"
    `build-scan`
    application
    kotlin("jvm", kotlinVerion)
    kotlin("kapt", kotlinVerion)
}

application {
    mainClassName = "samples.HelloCoroutinesKt"
}

kotlin { // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}


repositories {
    jcenter()
    google()
    maven { setUrl("https://jitpack.io") }
}

tasks {
    val copyToLib by creating(Copy::class) {
        from(configurations.runtime)
        into("lib")
    }

//    val klean by creating(Delete::class) {
//        delete = setOf("build/source/kapt", "build/source/kaptKotlin")
//    }


    // https://kotlinlang.slack.com/files/U1BASJRMW/F750V7R5G/task_for_setting_up_a_script_for_kshell.kt
    // Usage: gw kshell && bash build/kshell.sh
    val kshell by tasks.creating {
        dependsOn("assemble")
        doFirst {
            val buildscriptClasspath = rootProject.buildscript.configurations["classpath"]

            val embeddedableCompiler = buildscriptClasspath
                    .resolvedConfiguration
                    .resolvedArtifacts
                    .first { it.name == "kotlin-compiler-embeddable" }

            val jarLocation = embeddedableCompiler.file
            val mainClasspath = java.sourceSets["main"].runtimeClasspath.joinToString(separator = ":")
            val scriptContent = """#!/usr/bin/env bash
java -cp ${jarLocation.absolutePath} org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -cp $mainClasspath
"""
            val output = file("$buildDir/kshell.sh")
            output.writeText(scriptContent)
            output.setExecutable(true)
        }
    }
}

object libs {
   val kotlin = "1.1.4-3"
   val retrofit = "2.2.0"
   val okhttp = "3.5.0"
   val moshi = "1.4.0"
   val okio = "1.11.0"
   val rxjava = "1.2.7"
   val rxjava2 = "2.0.7"
   val rxkotlin = "1.0.0"
   val kotlintest = "1.3.7"
   val dagger = "2.9"
   val konfig = "1.5.0.0"
   val kotlinxhtml = "0.6.3"
   private val coroutineVersion = "0.18"
   val coroutineModules = listOf("core", "rx1", "rx2", "reactive", "reactor", "android", "javafx", "swing", "jdk8", "nio", "guava", "quasar")

    /** [Retrofit](http://square.github.io/retrofit) dependency
     *
     * [module] can be retrofit, retrofit converter-moshi adapter-rxjava retrofit-mock adapter-rxjava2
    converter-wire converter-jackson converter-gson converter-simplexml converter-protobuf
     *
     * See [Future Studio](https://futurestud.io/tutorials/retrofit-getting-started-and-android-client)
     * */
    fun retrofit2(module: String): Any =
            "com.squareup.retrofit2:$module:$retrofit"

    /** OkHttp where [module] can be okhttp, logging-interceptor, mockwebserver
     *
     * See [Github](https://github.com/square/okhttp) [Recipes](https://github.com/square/okhttp/wiki/Recipes) **/
    fun okhttp(module: String) : Any =
            "com.squareup.okhttp3:$module:$okhttp"

    /** Kotlinx.coroutines dependancy where [module] one of [coroutineModules] **/
    fun coroutine(module: String) : String {
        check(module in coroutineModules)
        return "org.jetbrains.kotlinx:kotlinx-coroutines-$module:$coroutineVersion"
    }
}

dependencies {

    // https://github.com/kotlintest/kotlintest
    testCompile("io.kotlintest:kotlintest:${libs.kotlintest}")
    testCompile(kotlin("test", libs.kotlin))
    testCompile(kotlin("test-junit", libs.kotlin))
    testCompile("junit:junit:4.11")
    testCompile("org.mockito:mockito-core:2.7.21")
    testCompile("com.nhaarman:mockito-kotlin:1.4.0")

    compile(kotlin("stdlib", libs.kotlin))
    compile(kotlin("reflect", libs.kotlin))

    for (module in listOf("retrofit", "converter-moshi", "adapter-rxjava", "retrofit-mock", "adapter-rxjava2")) {
        compile(libs.retrofit2(module))
    }
    for (module in listOf("okhttp", "logging-interceptor", "mockwebserver")) {
        compile(libs.okhttp(module))
    }

    // coroutines
    compile(libs.coroutine("core"))
    compile(libs.coroutine("rx2"))

    // https://github.com/gildor/kotlin-coroutines-retrofit
    compile("ru.gildor.coroutines:kotlin-coroutines-retrofit:0.5.0")

    // https://github.com/square/moshi
    compile("com.squareup.moshi:moshi:${libs.moshi}")

    // https://github.com/square/okio
    compile("com.squareup.okio:okio:${libs.okio}")

    // https://google.github.io/dagger/
//    compile("com.google.dagger:dagger:${libs.dagger}")
//    kapt("com.google.dagger:dagger-compiler:${libs.dagger}")

    compile("com.github.jmfayard:restinparse:master-SNAPSHOT")

    // http://jtwig.org/documentation/reference
    compile("org.jtwig:jtwig-core:5.85.3.RELEASE")
    compile(group = "org.slf4j", name = "slf4j-simple", version= "1.7.25")
//    compile(group = "org.slf4j", name = "slf4j-log4j12", version = "1.7.21")



    // https://github.com/Kotlin/kotlinx.html/wiki/Getting-started
    compile("org.jetbrains.kotlinx:kotlinx-html-jvm:${libs.kotlinxhtml}")


    // Result for Railway Oriented Programming
    // https://github.com/kittinunf/Result
    // https://www.slideshare.net/ScottWlaschin/railway-oriented-programming
    compile("com.github.kittinunf.result:result:1.1.0")


//    compile("org.jetbrains:annotations:15.0")

    // https://github.com/npryce/konfig
    compile("com.natpryce:konfig:${libs.konfig}")

    // http://www.joda.org/joda-time/userguide.html
    compile("joda-time:joda-time:2.9.3")

    // https://github.com/zeroturnaround/zt-exec
    compile(group= "org.zeroturnaround", name= "zt-exec", version= "1.9")

    // https://github.com/Kotlin/kotlinx.html/wiki/Getting-started
    compile("org.jetbrains.kotlinx:kotlinx-html-jvm:${libs.kotlinxhtml}")


    // Result for Railway Oriented Programming
    // https://github.com/kittinunf/Result
    // https://www.slideshare.net/ScottWlaschin/railway-oriented-programming
    compile("com.github.kittinunf.result:result:1.1.0")


    compile("com.google.code.findbugs:jsr305:3.0.2")

    // https://github.com/MiloszKrajewski/stateful4k
    compile("com.github.MiloszKrajewski:stateful4k:master")


    // JSR305
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    testCompileOnly("com.google.code.findbugs:jsr305:3.0.2")

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
