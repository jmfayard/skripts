import com.natpryce.konfig.*
import com.stripe.Stripe
import com.stripe.model.Charge
import io.kotlintest.specs.StringSpec
import java.io.File


/**
Tokens can be generated here https://stripe.com/docs/checkout/tutorial
4000002500000003 France (FR)
4242424242424242 Visa
5200828282828210 Mastercard (debit)
4000000000000127 Charge is declined with an incorrect_cvc code.
 */
object stripe: PropertyGroup() {

    object test: PropertyGroup() {
        val secret by stringType
        val public by stringType
    }
}


class TestStripe : StringSpec() { init {

    val config = EnvironmentVariables() overriding ConfigurationProperties.fromFile(File("config.properties"))

    val token = "tok_19xGTQ2eZvKYlo2CtJDQHioc"
    val apiKey = config[stripe.test.secret].debug("secret")
    Stripe.apiKey = apiKey


    val params = mapOf(
            "amount" to 1000,
            "currency" to "usd",
            "description" to "DHD is great",
            "source" to token
    )

    "Cards" {
        val id = "card_19xGTQ2eZvKYlo2C5Etnxaz0"
        Charge.retrieve(id).debug("Charge")
    }

    "Testing stripe" {
        try {
            val charge = Charge.create(params)
            charge.debug("charge")
            charge.outcome.type shouldBe "authorized"
        } catch (e: Exception) {
            e.message.debug("ERROR")
            throw e

        }

    }

}}