import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.StringSpec
import org.iban4j.*
import java.math.BigInteger


class TestIbans: FreeSpec()  {

    init {

        val validIbans = listOf(
                "BE62510007547061",
                "DK5000400440116243",
                "FR1420041010050500013M02606",
                "DE89370400440532013000",
                "NL39RABO0300065264",
                "SI56191000000123438",
                "ES8023100001180000012345",
                "GB29 NWBK 6016 1331 9268 19"
        )

        "Valid ibans" - {

        for (s in validIbans) {
            "$s iban4j" {
                IbanUtil.validate(s)
                val iban = Iban.valueOf(s)
                "iban=$s => ${iban.countryCode.alpha2}, ${iban.bankCode}, ${iban.accountNumber} ${iban.branchCode}"
            }

            "$s selfie" {
                validIban(s) shouldBe true
            }
        }
        }

        "Check invalid ibans" - {

            "no iban given" {
                shouldThrow<IbanFormatException> {
                    IbanUtil.validate(null)
                }
            }

            for (s in validIbans) {
                val invalid = s.replaceRange(s.count()-2, s.count(), "42")
                "$invalid iban4j" {
                    shouldThrow<InvalidCheckDigitException> {
                        IbanUtil.validate(invalid)
                    }
                }
                "$invalid selfie" {
                    validIban(s) shouldBe false
                }
            }

        }


        "Check valid bics" - {
            val validBics = listOf("COBADEHDXXX", "DEUTDEFF500", "BNPANL2A", "INSINL2A", "DEUTDEFF500")

            for (bic in validBics) {
                "Valid BIC: $bic" {
                    BicUtil.validate(bic)
                    Bic.valueOf(bic).debug("bic")
                }
            }
            "Parsing BIC" {
                val bic = Bic.valueOf("COBADEHDXXX")
                bic.bankCode shouldBe "COBA"
                bic.countryCode.alpha2 shouldBe "DE"
                bic.locationCode shouldBe "HD"
                bic.branchCode shouldBe "XXX"
            }
        }



        "Invalid BIC" {
            forAll<String> { wrongBic ->
                print(wrongBic)
                shouldThrow<BicFormatException> {
                    BicUtil.validate(wrongBic)
                    Bic.valueOf(wrongBic)
                }
                true
            }
            println()
        }



        shouldThrow<BicFormatException> {
            BicUtil.validate("ABC")
        }
        "Generating" {
            val iban = Iban.valueOf("DE89370400440532013000")
            iban.countryCode shouldBe CountryCode.DE
            iban.bankCode shouldBe "37040044"
            iban.accountNumber shouldBe "0532013000"
            val iban2 = Iban.Builder()
                    .countryCode(CountryCode.DE)
                    .bankCode("37040044")
                    .accountNumber("0532013000")
                    .build()
            iban shouldBe iban2

        }



    }
}

fun validIban(iban: String?): Boolean {
    var iban: String = iban ?: return false

    iban = iban.trim { it <= ' ' }.replace(" ", "")
    iban = iban.replace("	", "")

    if (iban.length < 15 || iban.length > 30) {
        return false
    }

    val firstLetter = iban[0] - 'A' + 10
    val secondLetter = iban[1] - 'A' + 10
    val thirdLetter = iban[2]
    val fourthLetter = iban[3]
    val shiftedIban = iban.substring(4) + Integer.toString(firstLetter) + Integer.toString(secondLetter) + thirdLetter + fourthLetter

    try {
        val numericIban = BigInteger(shiftedIban)
        return numericIban.mod(BigInteger("97")) == BigInteger.ONE
    } catch (e: NumberFormatException) {
        return false
    }

}