
package dagger_coffee

import dagger.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.test.assertEquals


fun main(args: Array<String>) {
    val coffee = DaggerCoffee.builder().build()
    coffee.maker().brew()
    assertEquals(Logger.messages, listOf(
            "~ ~ ~ heating ~ ~ ~",
            "=> => pumping => =>",
            "[_]P coffee! [_]P"
    ))
}

/** Our fancy coffee maker, built with dependancy injection*/
class CoffeeMaker
@Inject constructor(val heater: Lazy<Heater>, val pump: Pump) {
    fun brew() {
        heater.get().on()
        pump.pump()
        Logger.say("[_]P coffee! [_]P")
        heater.get().off()
    }
}

/** Dependancies are defined by those interfaces **/
interface Pump {
    fun pump()
}

interface Heater {
    fun on()
    fun off()
    val isHot: Boolean
}

/** Dagger setup that provides our injected objects **/
@Component(modules = arrayOf(DripCoffeeModule::class))
@Singleton interface Coffee {
    fun maker(): CoffeeMaker

}

@Module(includes = arrayOf(PumpModule::class))
class DripCoffeeModule {

    @Provides @Singleton
    fun provideHeater() = object : Heater {
        override var isHot: Boolean = false

        override fun on() {
            Logger.say("~ ~ ~ heating ~ ~ ~")
            isHot = true
        }

        override fun off() {
            isHot = false
        }
    }
}

@Module
abstract class PumpModule {

    /** Second concret object is also built by dependancy injection **/
    abstract @Binds fun providePump(pump: Thermosiphon): Pump
}


class Thermosiphon
@Inject constructor(val heater: Heater) : Pump {

    override fun pump() {
        if (heater.isHot) {
            Logger.say("=> => pumping => =>")
        }
    }
}


/** Allows us to test the output inside main() **/
object Logger {
    val messages = mutableListOf<String>()

    fun say(message: String) {
        messages += message
        println(message)
    }
}






