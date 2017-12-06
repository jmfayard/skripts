package nulls

fun main(args: Array<String>) {
    val java = JavaClass()

    /* KO: Expected type is not null in java but may be null in kotlin */
//    java.nullableSearch(null, null)

    /* KO: Type mismatch, expected String, found String?  */
//    val s : String = java.nullableSearch("foo", "bar")

    /* OK */
    val foundOrNull = java.nullableSearch(null, "something")

    /* KO: only safe or null-asserted call for String? */
//    println(foundOrNull.length)

    println(foundOrNull?.length)

    val greeting = java.greeting("Jake")

    /* Warning: un-neccessary safe-call on a non-null receiver of type String */
//    println(greeting?.length)

    /* OK */
    println(greeting.length)


}