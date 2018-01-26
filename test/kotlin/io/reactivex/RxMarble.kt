package io.reactivex

import com.google.common.annotations.VisibleForTesting
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit


const val TICK = 1000L


fun TestScheduler.advanceByFrame(nb: Int, tickMs: Long = TICK): TestScheduler {
    this.advanceTimeBy(tickMs * nb, TimeUnit.MILLISECONDS)
    return this
}


class RxMarble(val tickMs: Long = 15L, val scheduler: Scheduler = TestScheduler(), val max: Long = Long.MAX_VALUE) {

    fun <T> cold(marbles: String, operation: (Int) -> T): Observable<T> {
        checkMarbles(marbles)
        val numbers = 0 until (parseNumbers(marbles).max() ?: 1)
        val mapping: List<T> = try {
            numbers.map(operation)
        } catch (e: Throwable) {
            throw IllegalArgumentException("Marble [$marbles] : mapping function throwed $e")
        }
        return cold(marbles, mapping)
    }

    fun <T> cold(marbles: String, mapping: List<T>): Observable<T> {
        checkMarbles(marbles)
        val invalidNumbers = parseNumbers(marbles).filter { it !in 1..mapping.size }
        require(invalidNumbers.isEmpty()) { "Marble [$marbles]: indexes $invalidNumbers not found in list $mapping" }
        return create(marbles, hot = false).map { mapping[it] }
    }

    fun <T> marbleOf(observable: Observable<T>, max: Int = 30) : Pair<String, List<T>> {
        val testScheduler = if (scheduler is TestScheduler) scheduler else error("this is not a test scheduler")
        val observer = observable.test()
        var lastValues : List<T> = emptyList()
        var lastCount = 0
        var found: Int = 0
        val marble = buildString {
            for (tick in 0 until max) {
                append(marbleNewEvents(observer, lastCount))
                if (observer.isTerminated) break
                lastCount = observer.valueCount()
                testScheduler.advanceTimeBy(tickMs, TimeUnit.MILLISECONDS)


//                val newCount = observer.valueCount().debug("Count$tick: ")
//                if (observer.errorCount() > 0) {
//                    append('#')
//                    break
//                } else if (observer.isTerminated) {
//                    append('|')
//                    break
//                } else if (newCount > lastCount) {
//                    append(found+1)
//                } else  {
//                    append('-')
//                }

//                lastCount = observer.valueCount().debug("lastCount")
//                found = observer.valueCount()
//                testScheduler.advanceTimeBy(tickMs, TimeUnit.MILLISECONDS)
            }
        }

        return marble.padEnd(max, '-') to observer.values()
    }

    private fun <T> marbleNewEvents(observer: TestObserver<T>, oldCount: Int) : String {
        var events =  mutableListOf<String>()

        observer.values().forEachIndexed { i, v ->
            if (i >= oldCount) events.add((i+1).toString())
        }
//        var i = oldValues.size
//        for (v in observer.values() - oldValues) {
//            i++
//
//        }
        if (observer.completions() > 0L) events.add("|")
        if (observer.errors().isNotEmpty()) events.add("#")
        return when {
            events.isEmpty() -> "-"
            events.size == 1 -> events.first()
            else -> events.joinToString()
        }
    }

    internal fun parseNumbers(marbles: String) : List<Int> {
        return marbles.split(*OTHERS).mapNotNull { it.toIntOrNull() }.distinct().sorted()
    }

    @VisibleForTesting
    internal fun checkMarbles(marbles: String, hot: Boolean = false) {
        val invalidChars = marbles.filterNot { it in NUMBERS || it in OTHERS }
        require(invalidChars.isEmpty()) {
            "Marble: [$marbles] contains invalid characters [$invalidChars]" }
        require(marbles.filter { it == '^' }.count() <= 1) {
            "Marble [$marbles]  contains multiple subscriptions" }
        require(marbles.filter { it == '#' || it == '|' }.count() <= 1) {
            "Marble [$marbles] contains multiple terminal events" }
        require(hot || !marbles.contains('^')) {
            "Marble [$marbles] is cold but contains a subscription point"
        }
    }

    fun firstFrame(marbles: String, hot: Boolean): Long {
        val first = if (hot) marbles.indexOfFirst { it == '^' } else -1
        return if (first == -1) 0L else first.toLong()
    }

    fun lastFrame(marbles: String, hot: Boolean): Long {
        val end = marbles.indexOfFirst { it == '#' || it == '|' }
        return if (end == -1) max else end.toLong()
    }

    fun completion(marbles: String): Observable<Int> {
        val end = marbles.firstOrNull { it == '#' || it == '|' }
        return when (end) {
            null -> Observable.never()
            '#' -> Observable.error(Error)
            '|' -> Observable.empty()
            else -> TODO("invalid completion ${completion(marbles)}")
        }
    }

    fun create(marbles: String, hot: Boolean): Observable<Int> {
        return Observable.merge(Observable.interval(tickMs, TimeUnit.MILLISECONDS, scheduler)
                .take(lastFrame(marbles, hot))
//                .skip(firstFrame(marbles, hot))
                .map { l -> marbles.getOrNull(l.toInt()) ?: "-" }
                .filter { it in NUMBERS }
                .map { "$it".toInt() },
                completion(marbles))
    }

    companion object {
        private val NUMBERS = '0'..'9'
        private val OTHERS = charArrayOf('|', '^', '#', '-', '(', ')')
    }

    object Error : RuntimeException("error")
}