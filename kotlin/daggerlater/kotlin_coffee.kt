#!/usr/bin/env kotlin-script.sh

package daggerlater

interface Pump {
  fun pump()
}

interface Heater {
  fun on()
  fun off()
  val isHot: Boolean
}

// Inspired by dagger2 components
interface CoffeComponent {
  val coffee: Coffee
  val heater: Heater
  val pump: Pump
  val maker: CoffeeMaker
}

var FANCY: Boolean = true

fun component(): CoffeComponent =
  if (FANCY) FancyCoffeeComponent
  else BoringCoffeeComponent

fun main(args: Array<String>) {

  FANCY = false
  val boringCoffee = component().coffee
  boringCoffee.maker.brew()
  assertEquals(
    Logger.messages, listOf(
      "[_]P coffee! [_]P"
    )
  )

  Logger.messages.clear()

  FANCY = true
  val kotlinCoffee = component().coffee
  kotlinCoffee.maker.brew()
  assertEquals(
    Logger.messages, listOf(
      "~ ~ ~ heating ~ ~ ~",
      "=> => pumping => =>",
      "[_]P coffee! [_]P"
    )
  )

  Logger.messages.clear()

  val daggerCoffee = Coffee()
  daggerCoffee.maker.brew()
  assertEquals(
    Logger.messages, listOf(
      "~ ~ ~ heating ~ ~ ~",
      "=> => pumping => =>",
      "[_]P coffee! [_]P"
    )
  )
}

fun assertEquals(actual: Any?, expected: Any?) {
  if (actual != expected) {
    error("Expected: $expected\nGot: $actual")
  }
}

// Equivalents of dagger2 modules
data class Coffee(val maker: CoffeeMaker = component().maker)

data class CoffeeMaker(
  val heater: Heater = component().heater,
  val pump: Pump = component().pump
) {
  fun brew() {
    heater.on()
    pump.pump()
    Logger.say("[_]P coffee! [_]P")
    heater.off()
  }
}

object FancyCoffeeComponent : CoffeComponent {
  override val heater: Heater = FancyHeater
  override val pump: Pump = Thermosiphon(heater)
  override val maker: CoffeeMaker by lazy {
    CoffeeMaker(heater, pump)
  }
  override val coffee: Coffee by lazy {
    Coffee(maker)
  }
}

data class Thermosiphon(
  val heater: Heater = component().heater
) : Pump {

  override fun pump() {
    if (heater.isHot) {
      Logger.say("=> => pumping => =>")
    }
  }
}

object FancyHeater : Heater {
  override var isHot: Boolean = false

  override fun on() {
    Logger.say("~ ~ ~ heating ~ ~ ~")
    isHot = true
  }

  override fun off() {
    isHot = false
  }
}

object BoringPump : Pump {
  override fun pump() = Unit
}

object BoringHeater : Heater {
  override fun on() = Unit
  override fun off() = Unit
  override val isHot: Boolean = false
}

object BoringCoffeeComponent : CoffeComponent {
  override val heater: Heater = BoringHeater
  override val pump: Pump = BoringPump
  override val maker: CoffeeMaker by lazy { CoffeeMaker(heater, pump) }
  override val coffee: Coffee by lazy { Coffee(maker) }
}

/** Allows us to test the output inside main() **/
object Logger {
  val messages = mutableListOf<String>()

  fun say(message: String) {
    messages += message
    println(message)
  }
}
