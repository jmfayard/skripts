#!/usr/bin/env kotlin-script.sh
package hocon

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.extract
import io.github.config4k.toConfig


fun main(args: Array<String>) {
    typesafeConfig()
    config4k()
}

// https://github.com/config4k/config4k
fun config4k() {
    val config: Config  = ConfigFactory.load("config4k.conf")
    val myFamily = Family(listOf(Person("foo", 20), Person("bar", 25)))
    config.extract<Family>("family") shouldBe myFamily
    config.extract<Map<String, Int>>("map") shouldBe mapOf("bar" to 6, "foo" to 5)
    config.extract<Int?>("maybeInt1") shouldBe 6
    config.extract<Int?>("maybeInt2") shouldBe null
    config.extract<Size>("size") shouldBe Size.SMALL

    val jsonExport = myFamily.toConfig("family").root().render(ConfigRenderOptions.concise())
    println(jsonExport)
}

internal data class Person(val name: String, val age: Int)
internal data class Family(val list: List<Person>)
enum class Size { SMALL, MEDIUM, LARGE }











internal  fun typesafeConfig() {
    // example of how system properties override; note this
    // must be set before the config lib is used
    System.setProperty("simple-lib.whatever", "This value comes from a system property")

    // Load our own config values from the default location,
    // application.conf
    val conf = ConfigFactory.load()
    System.out.println("The answer is: " + conf.getString("simple-app.answer"))

    // In this simple app, we're allowing SimpleLibContext() to
    // use the default config in application.conf ; this is exactly
    // the same as passing in ConfigFactory.load() here, so we could
    // also write "new SimpleLibContext(conf)" and it would be the same.
    // (simple-lib is a library in this same examples/ directory).
    // The point is that SimpleLibContext defaults to ConfigFactory.load()
    // but also allows us to pass in our own Config.
    val context = SimpleLibContext()
    context.printSetting("simple-lib.foo")
    context.printSetting("simple-lib.hello")
    context.printSetting("simple-lib.whatever")
}



// we have a constructor allowing the app to provide a custom Config
internal class SimpleLibContext(val config: Config = ConfigFactory.load()) {

    init {
        // This verifies that the Config is sane and has our
        // reference config. Importantly, we specify the "simple-lib"
        // path so we only validate settings that belong to this
        // library. Otherwise, we might throw mistaken errors about
        // settings we know nothing about.
        config.checkValid(ConfigFactory.defaultReference(), "simple-lib")
    }

    // this is the amazing functionality provided by simple-lib
    fun printSetting(path: String) {
        println("The setting '" + path + "' is: " + config.getString(path))
    }
}// This uses the standard default Config, if none is provided,
// which simplifies apps willing to use the defaults



@Suppress("NOTHING_TO_INLINE")
infix fun <T> T.shouldBe(expected: T) {
    val actual = this
    println("Checking: $expected")
    check(actual == expected) { "it shouldBe but isn't\nEXPECTED: $expected\nBUT GOT : $actual" }
}