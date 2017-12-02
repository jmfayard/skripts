

import com.squareup.kotlinpoet.*
import krangl.*

// todo convert to script

/** Reproduce popular sleep data workflow with krangl*/



fun main(args: Array<String>) {

    // but for sake of learning the API we load it from file here
    val sleepData  = DataFrame.fromCSV(resourceFile("msleep.csv"))

    // select columns of interest
    val slimSleep = sleepData.select("name", "sleep_total")

    // Negative selection (aka column removal)
    sleepData.remove("conservation")


    // Do a range selection
    sleepData.select{ range("name", "order")}


    // Select all columns that start with the character string "sl" along with the `name` column, use the function `startsWith()`:
    sleepData.select({listOf("name")}, { startsWith("sl")})


    //
    // Filter rows with `filter`
    //

    // Find those animals that sleep more than 16h hour
    sleepData.filter { it["sleep_total"] gt 16} // which is a shortcut for:
    sleepData.filter { it["sleep_total"].greaterThan(16) }.print()

    val data: DataFrame = sleepData.filter { it["sleep_total"] gt 16}

    val sheepClass: ClassName = Sheep::class.asClassName()
    val kSheeps = data.rows.map { row: DataFrameRow ->
        CodeBlock.of("%T(%S, %S, %L)", sheepClass, row["name"], row["genus"], row["awake"])
    }




    val sheepProperty =                     PropertySpec.builder("sheeps", ParameterizedTypeName.get(List::class.asClassName(), sheepClass))
            .addKdoc("All the sheeps")
            .initializer(kSheeps.joinToCode(prefix = "listOf(\n", suffix = "\n)", separator = ",\n"))
            .build()

    val sheepSpec = DataClass("Sheep",
            Val("name", STRING).withDefault(),
            Val("genus", STRING).withDefault(),
            Val("awake", DOUBLE).withDefault(),
            mutable = false
    )


    val file = FileSpec.builder("krangl", "sheeps.kt")
            .addComment("Sheeps as code")
            .addType(sheepSpec)
            .addProperty(sheepProperty)
            .build()
    print(file)

}



data class Sheep(val name: String, val genus: String, val awake: Double)
