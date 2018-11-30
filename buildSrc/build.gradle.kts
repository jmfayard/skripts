plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}


dependencies {
    /** Fluent Assertion-Library for Kotlin https://markusamshove.github.io/Kluent/ **/
    testImplementation("org.amshove.kluent:kluent:1.4")
    testImplementation("org.testng:testng:6.14.3")

}

tasks.withType(Test::class.java).configureEach {
    useTestNG()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
