package variances

data class Producer<out T : Beverage>(
    val beverage: T
) {
    fun produce(): T = beverage
}

class Consumer<in T : Beverage> {
    fun consume(t: T) = println("Thanks for the drink $t!")
}

interface Beverage
object Coffee : Beverage
object Whisky : Beverage

fun main(args: Array<String>) {
    val colombia: Producer<Coffee> = Producer(Coffee)
    val scottland: Producer<Whisky> = Producer(Whisky)
//    val noCoffeeThere : Coffee = scottland.produce() // error

    val beverages: List<Beverage> = listOf(colombia, scottland).map { it.produce() }

    val starbucks = Consumer<Coffee>()
    starbucks.consume(colombia.produce())
//    starbucks.consume(scottland.produce()) // error

    val pub = Consumer<Whisky>()
    pub.consume(scottland.produce())
//    pub.consume(colombia.produce()) // error
}
