package nulls

fun main(args: Array<String>) {

    val t = Test()

    t.parameterNotNull(null)
    t.parameterNotNull(null)
    t.maybeNull1().boum()
    t.maybeNull2()?.boum()
    t.neverNull().boum()

}