import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Jsr310Tests : FreeSpec() { init {

    "Formatters" {
        val epoch: ZonedDateTime = Instant.EPOCH.atZone(ZoneOffset.UTC)
        epoch.toString() shouldBe "1970-01-01T00:00Z"

        LocalDate.from(epoch).toString() shouldBe "1970-01-01"

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        LocalDate.from(epoch).format(dateFormatter) shouldBe "1970/01/01"

        ZonedDateTime.parse("1970-01-01T00:00Z") shouldBe epoch

        val yyyyMMdd = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        LocalDate.parse("1970/01/01", yyyyMMdd).atStartOfDay(ZoneOffset.UTC) shouldBe epoch

        println(ZonedDateTime.parse("2017-09-27T14:08:27.948Z"))
    }
}
}
