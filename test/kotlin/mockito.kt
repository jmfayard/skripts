import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.specs.FreeSpec

private interface Logger {
    fun log(message: String)
}

private class DummyList(val list: List<Int>, val logger: Logger) : List<Int> by list {

    init {
        logger.log("Initialized")
    }

    fun second(): Int {
        val second = list[1]
        logger.log("second: $second")
        return second
    }

    fun third(): Int {
        logger.log("third: ${list[2]}")
        return list[2]
    }

    fun fifth(): Int = TODO()
}

interface Foo {
    var bar: String
}

class MockitoTests : FreeSpec() { init {

    "Given a mocked logger" - {
        /* Given */
        val logger = mock<Logger>()
        val ints = List(10) { it * it }
        val list = DummyList(ints, logger)

        "On initialized" {
            verify(logger).log(eq("Initialized"))
        }

        "On accessing the third element" {
            list.third() shouldEqual 4
            verify(logger).log(eq("third: 4"))
        }
    }

    "Given a mocked list" - {
        val list = mock<List<Int>>(verboseLogging = true, name = "list")

        whenever(list.get(any())).then { mock -> mock.getArgument<Int>(0) + 1 }
        whenever(list.size) doReturn 4

        val dummy = DummyList(list, mock())
        "Stubbing and spying" {
            dummy.second() shouldEqual 2
            dummy.third()
            dummy[4] shouldEqual 5
            dummy.size shouldEqual 4
        }
        "Verifying" {
            verify(list).get(1)
            verify(list).get(4)
            verify(list).size
            verify(list, times(2)).get(2)
            verify(list, never()).get(10)
        }
    }

    "Testing properties" {
        val foo = mock<Foo>()
        foo.bar = "set"
        verify(foo).bar = "set"
    }
}
}
