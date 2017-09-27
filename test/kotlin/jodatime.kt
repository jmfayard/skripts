import io.kotlintest.matchers.be
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.StringSpec
import org.joda.time.Instant
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime


/***
 *
 * The five date-time classes that will be used most are:

Instant - Immutable class representing an instantaneous point on the time-line
DateTime - Immutable replacement for JDK Calendar
LocalDate - Immutable class representing a local date without a time (no time-zone)
LocalTime - Immutable class representing a time without a date (no time-zone)
LocalDateTime - Immutable class representing a local date and time (no time-zone)

An Instant is a good class to use for the timestamp of an event, as there is no calendar system or time-zone to worry about.
A LocalDate is a good class to use to represent a date of birth, as there is no need to refer to the time of day.
A LocalTime is a good class to use to represent the time of day that a shop opens or closes.
A DateTime is a good class to use as a general purpose replacement for the JDK Calendar class, where the time-zone information is important.

See http://www.joda.org/joda-time/quickstart.html

 */
class JodaTimeTests: FreeSpec() { init {



    "Instant - Immutable class representing an instantaneous point on the time-line" - {
        "Epoch" {
            val epoch = Instant(0)
            epoch.millis shouldBe 0L
            epoch.toString() shouldBe "1970-01-01T00:00:00.000Z"
        }

        "Now" {
            val now = Instant()
            println("It's now: $now")
            now.toDateTime().toLocalDateTime().year should be gte 2017
            now.toDateTime().toLocalDateTime().yearOfCentury should be gte 17
        }

        "Parsing" {
            val isoString = "2017-09-27T12:27:23.273Z"
            val then = Instant.parse(isoString)
            then.millis shouldBe 1506515243273L
        }
    }

    "LocalTime - Immutable class representing a time without a date (no time-zone)" {

        val openingTime = LocalTime(10, 0)
        openingTime.toString() shouldBe "10:00:00.000"

        openingTime.toString("hh:mm").also { displayTime ->
            displayTime shouldBe "10:00"
            println("My grocery opens at $displayTime")
        }

        LocalTime.parse("10:00") shouldBe openingTime

    }

    "LocalDate - Immutable class representing a local date without a time (no time-zone)" {
        val birthday = LocalDate(1981, 12, 29)
        birthday.monthOfYear shouldBe 12
        birthday.toString() shouldBe "1981-12-29"
        birthday.toString("dd/MM/yyyy") shouldBe "29/12/1981"
    }

    "LocalDateTime - Immutable class representing a local date and time (no time-zone)" {
        val concertStart = LocalDateTime(2017, 9, 27, 20, 0)
        concertStart.toString() shouldBe "2017-09-27T20:00:00.000"
        LocalDateTime.parse("2017-09-27T20:00") shouldBe concertStart
    }

}}