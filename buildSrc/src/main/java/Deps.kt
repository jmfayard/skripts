@file:Suppress("NAME_SHADOWING")

object Deps {
    val versions = Versions

    /** [http://kotlinlang.org/](http://kotlinlang.org/)   */
    @JvmStatic
    fun Kotlin(vararg module: String): List<String> =
            module.requireIn(modulesKotlin)
                    .map { m -> "org.jetbrains.kotlin:$m:${Android.kotlin}" }

    val modulesKotlin = listOf("kotlin-stdlib", "kotlin-reflect", "kotlin-test",
            "kotlin-test-junit", "kotlin-stdlib-jdk8", "kotlin-stdlib-jdk7", "kotlin-stdlib-common",
            "gradle-api", "kotlin-allopen", "kotlin-jdk-annotations", "kotlin-android-extensions")

    /** **/
    @JvmStatic
    fun Coroutines(vararg module: String) : List<String> =
        module.requireIn(modulesCoroutines)
            .map { m -> "org.jetbrains.kotlinx:$m:${versions.coroutineVersion}" }

    val modulesCoroutines = listOf("kotlinx-coroutines-core", "kotlinx-coroutines-jdk8",
        "kotlinx-coroutines-reactive", "kotlinx-coroutines-rx2", "kotlinx-coroutines-reactor", "kotlinx-coroutines-android", "kotlinx-coroutines-nio")

    /**
     * [https://developer.android.com/topic/libraries/support-library/packages.html#recommendation](https://developer.android.com/topic/libraries/support-library/packages.html#recommendation)   */
    @JvmStatic
    fun AndroidSupport(vararg module: String): List<String> =
            module.requireIn(modulesAndroidSupport)
                    .map { m -> "com.android.support:$m:${versions.support}" }

    val modulesAndroidSupport = listOf("support-v4", "appcompat-v7", "preference-v7", "design", "percent", "cardview-v7", "customtabs", "gridlayout-v7", "support-annotations",
            "mediarouter-v7", "palette-v7", "recyclerview-v7", "preference-v7"
            , "preference-v14", "preference-leanback-v17", "leanback-v17", "support-vector-drawable", "animated-vector-drawable", "percent", "exifinterface",
            "recommendation", "wear", "support-compat", "support-core-utils",
            "support-core-ui", "support-media-compat", "support-fragment")

    /**
     * [https://developers.google.com/android/guides/overview](https://developers.google.com/android/guides/overview)
     * [https://developers.google.com/nearby/connections/overview](https://developers.google.com/nearby/connections/overview)
     **/
    @JvmStatic
    fun PlayServices(vararg module: String): List<String> =
            module.requireIn(modulesPlayServices)
                    .map { m -> "com.google.android.gms:$m:${versions.play}" }

    val modulesPlayServices = listOf("play-services-nearby", "play-services-maps")


    /** Functional companion to Kotlin"s Standard Library
     * [http://arrow-kt.io/docs/](http://arrow-kt.io/docs/)
     * [https://github.com/arrow-kt/arrow](https://github.com/arrow-kt/arrow)
     */
    @JvmStatic
    fun Arrow(vararg module: String): List<String> =
            module.requireIn(modulesArrow)
                    .map { m -> "io.arrow-kt:$m:${versions.arrow}" }

    val modulesArrow = listOf("arrow-core", "arrow-data", "arrow-typeclasses",
            "arrow-instances", "arrow-syntax", "arrow-annotations-processor", "arrow-free", "arrow-mtl",
            "arrow-effects", "arrow-effects-rx2", "arrow-effects-kotlinx-coroutines", "arrow-optics")


    /** Retrofit
    [http://square.github.io/retrofit](http://square.github.io/retrofit/)
    [https://futurestud.io/tutorials/retrofit-getting-started-and-android-client](https://futurestud.io/tutorials/retrofit-getting-started-and-android-client)
     **/
    @JvmStatic
    fun Retrofit(vararg module: String): List<String> =
            module.requireIn(modulesRetrofit)
                    .map { module -> "com.squareup.retrofit2:$module:${versions.retrofit}" }

    val modulesRetrofit = listOf("retrofit", "converter-moshi", "converter-gson",
            "converter-wire", "converter-jackson", "converter-simplexml", "converter-protobuf",
            "adapter-rxjava2", "retrofit-mock", "retrofit-adapters", "converter-guava", "adapter-rxjava")

    const val RetrofitRxjava2 = "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0"

    private fun Array<out String>.requireIn(modules: List<String>): List<String> {
        val invalids = this.filter { it !in modules }
        require(this.isNotEmpty() && invalids.isEmpty()) {
            "Invalid modules $invalids : Choose among " + modules.joinToString(prefix="\"", postfix = "\"", separator = "\", \"")
        }
        return this.toList()
    }

    /** Firebase Push Notifications     */
    /** BEEP-2 [https://git.mautinoa.com/long/apiSpec/merge_requests/2](https://git.mautinoa.com/long/apiSpec/merge_requests/2)     */
    const val FirebaseMessaging = "com.google.firebase:firebase-messaging:${versions.play}"

    /** Android runtime permissions powered by RxJava2     */
    /** [https://github.com/tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions)     */
    const val RxPermissions = "com.tbruyelle.rxpermissions2:rxpermissions:0.9.5@aar"

    /*** A logger with a small, extensible API which provides utility on top of Android's normal Log class.
     * [http://jakewharton.github.io/timber/](http://jakewharton.github.io/timber/)   */
    const val Timber = "com.jakewharton.timber:timber:" + versions.timber

    /**Magellan, the Simplest Navigation for Android
    [https://github.com/wealthfront/magellan](https://github.com/wealthfront/magellan) **/
    const val Magellan = "com.wealthfront:magellan:" + versions.magellan
    const val MagellanSupport = "com.wealthfront:magellan-support:" + versions.magellan
    const val MagellanRx = "com.wealthfront:magellan-rx:" + versions.magellan

    /** [https://github.com/ReactiveX/RxAndroid](https://github.com/ReactiveX/RxAndroid) **/
    const val RxAndroid = "io.reactivex.rxjava2:rxandroid:" + versions.rxandroid


    /** Use Espresso to write concise, beautiful, and reliable Android UI tests.     */
    /** [https://developer.android.com/training/testing/espresso/index.html](https://developer.android.com/training/testing/espresso/index.html)     */
    const val EspressoCore = "com.android.support.test.espresso:espresso-core:" + versions.espresso
    const val EspressoContrib = "com.android.support.test.espresso:espresso-contrib:" + versions.espresso
    const val EspressoTestRunner = "com.android.support.test:runner:1.0.1"

    /** [http://javamoney.github.io/](http://javamoney.github.io/)     */
    const val JavaMoney = "org.javamoney:moneta-bp:1.1"


    /** Apps over 64k methods
     * [https://developer.android.com/studio/build/multidex.html](https://developer.android.com/studio/build/multidex.html)     */
    const val Multidex = "com.android.support:multidex:" + versions.multidex

    /** JSR-305 nullability annotations: **/
    const val Jsr305 = "com.google.code.findbugs:jsr305:3.0.2"
    const val JavaxInject = "javax.inject:javax.inject:1"

    /** [https://developer.android.com/training/constraint-layout/index.html](https://developer.android.com/training/constraint-layout/index.html)     */
    const val ConstraintLayout = "com.android.support.constraint:constraint-layout:" + versions.constraint

    /**
    [http://rxmarbles.com/](http://rxmarbles.com/)
    [http://reactivex.io/documentation/operators.html](http://reactivex.io/documentation/operators.html)
    [https://github.com/ReactiveX/RxJava/wiki](https://github.com/ReactiveX/RxJava/wiki)
     */
    const val RxJava2 = "io.reactivex.rxjava2:rxjava:" + versions.rxjava2


    /** [https://github.com/ReactiveX/RxKotlin](https://github.com/ReactiveX/RxKotlin)     */
    const val RxKotlin = "io.reactivex.rxjava2:rxkotlin:" + versions.rxkotlin2

    /** [https://github.com/JakeWharton/RxBinding](https://github.com/JakeWharton/RxBinding)     */
    const val RxBindingKotlin = "com.jakewharton.rxbinding2:rxbinding-kotlin:" + versions.rxbinding
    const val RxBindingV4Kotlin = "com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:" + versions.rxbinding


    /** [https://github.com/square/okhttp/wiki/Recipes](https://github.com/square/okhttp/wiki/Recipes)     */
    const val Okhttp = "com.squareup.okhttp3:okhttp:" + versions.okhttp
    const val OkhttpLogging = "com.squareup.okhttp3:logging-interceptor:" + versions.okhttp
    const val OkhttpMockserver = "com.squareup.okhttp3:mockwebserver:" + versions.okhttp

    /** [https://github.com/square/moshi](https://github.com/square/moshi)     */
    const val Moshi = "com.squareup.moshi:moshi:" + versions.moshi

    /** [https://github.com/square/okio](https://github.com/square/okio)     */
    const val Okio = "com.squareup.okio:okio:" + versions.okio

    /** Dagger is a fully static, compile-time dependency injection framework for both Java and Android. It is an adaptation of an earlier version created by Square and now maintained by Google.     */
    /** [https://google.github.io/dagger/](https://google.github.io/dagger/)     */
    const val Dagger = "com.google.dagger:dagger:" + versions.dagger2
    const val DaggerCompiler = "com.google.dagger:dagger:" + versions.dagger2


    /** Custom fonts in Android the easy way...     */
    /** [https://github.com/chrisjenx/Calligraphy](https://github.com/chrisjenx/Calligraphy)     */
    const val Calligraphy = "uk.co.chrisjenx:calligraphy:" + versions.calligraphy


    /**A slim & clean & typeable Adapter without# VIEWHOLDER  [https://github.com/MEiDIK/SlimAdapter](https://github.com/MEiDIK/SlimAdapter) */
    const val SlimAdapter = "net.idik:slimadapter:${versions.slimadapter}"


    /** [http://marcinmoskala.com/android/kotlin/2017/05/05/still-mvp-or-already-mvvm.html](http://marcinmoskala.com/android/kotlin/2017/05/05/still-mvp-or-already-mvvm.html)     */
    const val KotlinAndroidViewBindings = "com.github.MarcinMoskala:KotlinAndroidViewBindings:" + versions.KotlinAndroidViewBindings


    /** [https://developer.android.com/topic/libraries/architecture/adding-components.html](https://developer.android.com/topic/libraries/architecture/adding-components.html)     */
    const val RoomRuntime = "android.arch.persistence.room:runtime:" + versions.architecture
    const val RoomCommon = "android.arch.persistence.room:common:" + versions.architecture
    const val RoomRxjava2 = "android.arch.persistence.room:rxjava2:" + versions.architecture
    const val RoomCompiler = "android.arch.persistence.room:compiler:" + versions.architecture
    const val RoomTesting = "android.arch.persistence.room:testing:" + versions.architecture

    /** [https://github.com/moove-it/fakeit](https://github.com/moove-it/fakeit)     */
    const val Fakeit = "com.github.moove-it:fakeit:" + versions.fakeit

    /**[https://github.com/afollestad/material-dialogs](https://github.com/afollestad/material-dialogs) */
    const val MaterialDialogs = "com.afollestad.material-dialogs:core:" + versions.materialDialogs


    /** [https://github.com/evernote/android-job](https://github.com/evernote/android-job)     */
    const val AndroidJob = "com.evernote:android-job:" + versions.androidjob

    /** [https://github.com/android/android-ktx](https://github.com/android/android-ktx)     */
    const val AndroidxCore = "androidx.core:core-ktx:" + versions.ktx

    /****** TESTING ****/

    /**
    KotlinTest DSL [https://github.com/kotlintest/kotlintest/blob/master/doc/reference.md](https://github.com/kotlintest/kotlintest/blob/master/doc/reference.md)
    KotlinTest Matchers: [https://github.com/kotlintest/kotlintest/blob/master/doc/matchers.md](https://github.com/kotlintest/kotlintest/blob/master/doc/matchers.md)
    Mockito-Kotlin [https://github.com/nhaarman/mockito-kotlin/wiki/Mocking-and-verifying](https://github.com/nhaarman/mockito-kotlin/wiki/Mocking-and-verifying)
     */
    const val KotlinTest = "io.kotlintest:kotlintest:" + versions.kotlintest
    const val JUnit = "junit:junit:" + versions.junit

    /** using kotlin with mockito [https://github.com/nhaarman/mockito-kotlin](https://github.com/nhaarman/mockito-kotlin)     */
    const val MockitoKotlin = "com.nhaarman:mockito-kotlin:" + versions.mockitoKotlin
    const val MockitoKotlinKt1 = "com.nhaarman:mockito-kotlin:" + versions.mockitoKotlin

    /**For @Nullable     */
    const val JetbrainsAnnotations = "org.jetbrains:annotations:15.0"


    /** Android backport of JSR-310 (java.time.* in Java8)
     * [https://github.com/JakeWharton/ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP)     */
    const val ThreeTenAbp = "com.jakewharton.threetenabp:threetenabp:1.0.5"

    /** git in java     */
    const val JgitCore = "org.eclipse.jgit:org.eclipse.jgit:3.7.1.201504261725-r"

    /** Simple annotation-based API to handle runtime permissions. [https://permissions-dispatcher.github.io](https://permissions-dispatcher.github.io)     */
    const val PermissionsDispatcher = "com.github.hotchemi:permissionsdispatcher:2.2.0"
    const val PermissionsCompiler = "com.github.hotchemi:permissionsdispatcher-processor:2.2.0"

    /** A set of AssertJ helpers geared toward testing Android. [http://square.github.io/assertj-android/](http://square.github.io/assertj-android/)     */
    const val AssertJAndroid = "com.squareup.assertj:assertj-android:1.0.0"


    /** An Espresso IdlingResource for OkHttp.     */
    /** [https://github.com/JakeWharton/okhttp-idling-resource  ](https://github.com/JakeWharton/okhttp-idling-resource  )   */
    const val OkhttpIdlingResource = "com.jakewharton.espresso:okhttp3-idling-resource:1.0.0"

    /** UI Automator is a UI testing framework suitable for cross-app functional UI testing across system and installed apps.     */
    /** [https://developer.android.com/training/testing/ui-automator.html  ](https://developer.android.com/training/testing/ui-automator.html  )   */
    const val AndroidUiAutomator = "com.android.support.test.uiautomator:uiautomator-v18:2.1.3"

    /**  [http://try.crashlytics.com/sdk-android/  ](http://try.crashlytics.com/sdk-android/  )   */
    const val Crashlytics = "com.crashlytics.sdk.android:crashlytics:2.6.8@aar"

    /** [https://github.com/nsk-mironov/kotlin-jetpack  ](https://github.com/nsk-mironov/kotlin-jetpack  )   */
    const val JetpackPreferences = "com.github.vmironov.jetpack:jetpack-bindings-preferences:${versions.jetpack}"
    const val JetpackArguments = "com.github.vmironov.jetpack:jetpack-bindings-arguments:${versions.jetpack}"


    /** [https://github.com/stanfy/spoon-gradle-plugin  ](https://github.com/stanfy/spoon-gradle-plugin  )   */
    const val SpoonClient = "com.squareup.spoon:spoon-client:1.6.4"

    /** Kotlin state machine library     */
    const val Stateful4k = "com.github.MiloszKrajewski:stateful4k:57c32592d0ca5ba36ef28af0f0fb6673f126edca"

    /** Android Support Library for NFC     */
    /** [https://github.com/fidesmo/nordpol  ](https://github.com/fidesmo/nordpol  )   */
    const val NordpolAndroid = "com.fidesmo:nordpol-android:0.1.22"

    /** https://www.bouncycastle.org/ **/
    const val BouncyCastleBcprov = "org.bouncycastle:bcprov-jdk15on:1.56"
    const val BouncyCastleBcpg = "org.bouncycastle:bcpg-jdk15on:1.56"

    /**    https://github.com/gildor/kotlin-coroutines-retrofit **/
    const val RetrofitCoroutines = "ru.gildor.coroutines:kotlin-coroutines-retrofit:" + versions.retrofitCoroutines

    const val MoshiLazyAdapters = "com.serjltt.moshi:moshi-lazy-adapters:" + versions.moshiLazyAdapters

    // http://jtwig.org/documentation/reference
    const val JTwig = "org.jtwig:jtwig-core:" + versions.jtwig

    // https://github.com/Kotlin/kotlinx.html/wiki/Getting-started
    const val KotlinXHtml = "org.jetbrains.kotlinx:kotlinx-html-jvm:" + versions.kotlinxhtml

    // https://github.com/npryce/konfig
    const val Konfig = "com.natpryce:konfig:" + versions.konfig

    // http://www.joda.org/joda-time/userguide.html
    const val JodaTime = "joda-time:joda-time:" + versions.jodatime

    // https://github.com/zeroturnaround/zt-exec
    const val ZeroTurnAround = "org.zeroturnaround:zt-exec:" + versions.zeroTurnAround

    // http://www.jdom.org/docs/apidocs/
    const val JDom = "org.jdom:jdom:" + versions.jdom

    const val Sl4J = "org.slf4j:slf4j-simple:" + versions.sl4j

    // data wrangling https://github.com/holgerbrandl/krangl
    const val Krangl = "de.mpicbg.scicomp:krangl:" + versions.krangl

    // https://square.github.io/kotlinpoet/0.x/kotlinpoet/com.squareup.kotlinpoet/
    const val KotlinPoet = "com.squareup:kotlinpoet:" + versions.kotlinPoet


    // https://github.com/edvin/tornadofx
    // https://www.gitbook.com/download/pdf/book/edvin/tornadofx-guide
    const val TornadoFx = "no.tornado:tornadofx:" + versions.tornadofx
    const val FontAwesomeFx = "de.jensd:fontawesomefx:" + versions.fontAwesomeFx
    const val ControlsFx = "org.controlsfx:controlsfx:" + versions.controlsFx
    const val MockitoCore = "org.mockito:mockito-core:${versions.mockito}"




}

