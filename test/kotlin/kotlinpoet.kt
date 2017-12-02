import com.squareup.kotlinpoet.*
import json2data.DataClass

val STRING = String::class.asClassName()


fun TypeSpec.Builder.Val(name: String, clazz: ClassName) {
    PropertySpec.builder("name", STRING).initializer("name").build()
}


fun Val(name: String, type: TypeName, vararg modifers: KModifier) =
        ParameterSpec.builder(name, type, *modifers)

fun ParameterSpec.Builder.withDefault(): ParameterSpec.Builder {
    val code = when (this.build().type) {
        STRING -> "\"\""
        INT -> "0"
        DOUBLE -> "0.0"
        else -> null
    }
    if (code != null) this.defaultValue(code)
    return this
}

fun DataClass(name: String, vararg params: ParameterSpec.Builder, mutable: Boolean = false): TypeSpec {
    val builtParams = params.map { it.build() }
    val properties = builtParams.map { param ->
        PropertySpec.builder(param.name, param.type)
                .initializer(param.name)
                .mutable(mutable)
                .build()
    }

    return TypeSpec.classBuilder(name)
            .primaryConstructor(
                    FunSpec.constructorBuilder().addParameters(builtParams).build()
            ).addProperties(properties)
            .build()

}