#!/usr/bin/env kotlin-script.sh
// Installation:  see kotlin-scripts/README.md

package dateme

import jmfayard.printAsTable
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatterBuilder
import org.joda.time.format.ISODateTimeFormat
import java.util.Locale

/***
 * Give the date in different formats
 *
 * Usage: $ date_me.kt [printDate|isodate|yyy-mm-dd|mysqldate]
 *
 * With no arguments, the current printDate is used

$ date_me.kt
1483624661                                seconds since EPOCH
1483624661563                             milliseconds since EPOCH
2017-01-05                                UTC time (simple format)
2017-01-05T13:57:41.563Z                  UTC time
2017-01-05 13:57:41                       UTC time (MySql format)
2017-01-05T14:57:41.563+01:00             Berlin Time
2017-01-05T08:57:41.563-05:00             Time in the JDK default time zone
jeudi 5 janvier 2017 13 h 57 UTC          fullDateTime() France

 * A specific date can be given in any of the formats above

$ date_me.kt 2017-01-05T13:57:41.563Z
$ date_me.kt 1981-12-29
$ date_me.kt "2017-01-05 13:57:41"
$ date_me.kt 148362466100
$ date_me.kt 1483624661
 *
 */
fun main(args: Array<String>) {

    val instant = if (args.isEmpty()) Instant.now() else parseDate(args[0])
    printDate(instant)
}

fun printDate(instant: Instant) {
    val utc = instant.toDateTime(DateTimeZone.UTC)
    val berlin = instant.toDateTime(DateTimeZone.forID("Europe/Berlin"))
    val default = instant.toDateTime(DateTimeZone.getDefault())

    printAsTable(
        instant.millis / 1000 to "seconds since EPOCH",
        instant.millis to "milliseconds since EPOCH",
        utc.toString(simpleDate) to "UTC time (simple format)",
        utc to "UTC time",
        utc.toString(mySql) to "UTC time (MySql format)",
        berlin to "Berlin Time",
        default to "Time in the JDK default time zone",
        DateTimeFormat.fullDateTime().withLocale(Locale.FRANCE).print(utc) to "fullDateTime() France"
    )
}

fun parseDate(input: String): Instant {
    if (input.isBlank()) {
        println("No argument given, using current printDate")
        return Instant.now()
    }
    if (input.all { it in '0'..'9' }) {
        val instantMS = Instant(input.toLong())
        val instantS = Instant(input.toLong().times(1000))
        if (instantMS.isAfter(DateTime(1971, 1, 1, 0, 0))) {
            return instantMS
        } else {
            return instantS
        }
    }
    val formatters = hashMapOf("iso" to ISODateTimeFormat.dateTimeParser(), "mysql" to mySql)
    for ((name, formatter) in formatters) {
        println("Trying formatter $name")
        try {
            return formatter.parseDateTime(input).toInstant()
        } catch (e: IllegalArgumentException) {
            println("Cannot parse with formatter $name")
        }
    }
    throw IllegalArgumentException("Cannot parse $input")
}

val mySql = DateTimeFormatterBuilder()
    .appendYear(4, 4)
    .appendLiteral('-')
    .appendMonthOfYear(2)
    .appendLiteral('-')
    .appendDayOfMonth(2)
    .appendLiteral(' ')
    .appendHourOfDay(2)
    .appendLiteral(':')
    .appendMinuteOfHour(2)
    .appendLiteral(':')
    .appendSecondOfMinute(2)
    .toFormatter()

val simpleDate = DateTimeFormatterBuilder()
    .appendYear(4, 4)
    .appendLiteral('-')
    .appendMonthOfYear(2)
    .appendLiteral('-')
    .appendDayOfMonth(2)
    .toFormatter()
