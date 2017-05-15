package result

import com.github.kittinunf.result.*
import io.kotlintest.matchers.be
import io.kotlintest.specs.StringSpec
import java.io.File
import java.io.IOException


class Tests : StringSpec() { init {

    "Railway oriented programmming" {
        Result.of {
            File("/path/to/file/foo.txt").readText()
        } should be a Result.Failure::class
        Result.of {
            File("build.gradle").readText()
        } should be a Result.Success::class
        val (value, _) = Result.of { 2 }.map { 3 }
        value shouldEqual 3


    }
    "Validation" {

        val r1: Result<Int, Exception> = Result.of(1)
        val r2: Result<Int, Exception> = Result.of{throw Exception("Not a number")}
        val r3: Result<Int, Exception> = Result.of(3)
        val r4: Result<Int, Exception> = Result.of{throw Exception("Division by zero")}

        val validation = Validation(r1, r2, r3, r4)
        validation.hasFailure shouldEqual true
        validation.failures.map{it.message} shouldEqual listOf("Not a number", "Division by zero")

    }

} }


data class Profile(val email: String, val username: String, val id: Int)

fun upload(p : Profile) : Unit {
    require(p.id < 0)
    println("Profile uploaded")
}

fun isEmailValid(email: String) : Boolean {
    return email.contains("@")
}

fun usernameIsTaken(username: String) : Boolean {
    return username == "peter"
}