#!/usr/bin/env kotlin-script.sh
package json2data

import com.squareup.moshi.Moshi
import okio.Buffer
import okio.Okio
import java.util.*


fun main(args: Array<String>) {

    val buffer = Okio.buffer(Okio.source(System.`in`))
    val json = buffer.readUtf8()
    println(json)

    val data = moshiAdapter.fromJson(json)
    val schema = schemaOf("root", data)
    val generator = KotlinPoet(schema)
    val classes = generator.dataClasses()
    val code = classes.map { generator.generateKotlinCode(it) }.joinToString(separator = "")
    println(code)


}


data class DataClass(val name: String, val properties: List<DataClassProp>)
data class DataClassProp(val name: String, val type: String, val defaultValue: String?, val required: Boolean)

sealed class Schema(val name: String, var type: String?) {
    override fun toString(): String = "Schmea($name, $type, ${this.javaClass.simpleName})"

    class O(name: String, val properties: Map<String, Schema>) : Schema(name, null)
    class L<out T : Schema>(name: String, val list: List<T>) : Schema(name, null)
    class I(name: String, val i: Int) : Schema(name, "Int")
    class D(name: String, val d: Double) : Schema(name, "Double")
    class B(name: String, val b: Boolean) : Schema(name, "Boolean")
    class S(name: String, val s: String) : Schema(name, "String")
    class N(name: String) : Schema(name, "Any?")
}


fun schemaOf(name: String, value: Any?): Schema = when (value) {
    null -> Schema.N(name)
    is Boolean -> Schema.B(name, value)
    is Number -> {
        val double = value.toDouble()
        if (double == Math.floor(double))
            Schema.I(name, value.toInt())
        else
            Schema.D(name, double)
    }
    is String -> Schema.S(name, value)
    is List<*> -> {
        val children = value.map { e -> schemaOf(name, e) }
        Schema.L(name, children)
    }
    is Map<*, *> -> {
        val children = value.mapValues { e ->
            schemaOf(e.key as String, e.value)
        }
        Schema.O(name, children as Map<String, Schema>)
    }
    else -> TODO("Unexpected value $value")
}


val json = """
{
  "address": {
    "streetAddress": "21 2nd Street",
    "city": "New York"
  },
  "phoneNumber": [
    {
      "location": "home",
      "code": 44
    }
  ],
  "price" : 98.245,
  "age": 24,
  "free": true,
  "name": "JMF",
  "something" : null
}
""".trim()

val moshi = Moshi.Builder().build()
val moshiAdapter = moshi.adapter(Map::class.java)

data class KotlinPoet(val schema: Schema) {

    init {
        var i = 0
        forAllTypes { s: Schema ->
            if (s.type == null) {
                s.type = "T${i++}"
            }
        }
    }

    fun forAllTypes(operation: (Schema) -> Unit) {
        val stack = Stack<Schema>()
        stack.addElement(schema)
        while (stack.isNotEmpty()) {
            val s = stack.pop()
            operation(s)
            if (s is Schema.O) {
                stack.addAll(s.properties.values)
            }
            if (s is Schema.L<*>) {
                stack.addAll(s.list)
            }
        }
    }


    fun defaultValue(schema: Schema): String? =
        when (schema) {
            is Schema.O -> null
            is Schema.L<*> -> "emptyList()"
            is Schema.I -> "0"
            is Schema.D -> "0.0"
            is Schema.B -> "false"
            is Schema.S -> "\"\""
            is Schema.N -> "null"
        }

    fun objectClass(schema: Schema.O): DataClass {
        val properties = schema.properties.entries.map {
            val (name: String, s: Schema) = it
            val type = if (s is Schema.L<*>) {
                val child = s.list.first()
                "List<${child.type}>"
            } else {
                s.type!!
            }
            DataClassProp(name, type, defaultValue(s), true)
        }
        return DataClass(schema.type!!, properties)
    }

    fun dataClasses(): List<DataClass> {
        val result = mutableListOf<DataClass>()
        forAllTypes { s: Schema ->
            if (s is Schema.O) {
                result += objectClass(s)
            }
        }
        return result
    }

    fun generateKotlinCode(c: DataClass) = buildString {
        append("data class ")
        append(c.name)
        append(" (")
        val props = c.properties
        props.forEachIndexed { i, prop ->
            append("\n    val ")
            append(prop.name)
            append(": ")
            append(prop.type)
            if (prop.defaultValue != null) {
                append(" = ")
                append(prop.defaultValue)
            }
            if (i != props.lastIndex) {
                append(",")
            }

        }
        append("\n")
        append(")")
        append("\n\n")
    }
}
