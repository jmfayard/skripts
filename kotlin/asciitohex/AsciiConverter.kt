package asciitohex

import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.encodeUtf8
import tornadofx.Controller
import tornadofx.setContent
import tornadofx.toProperty

class AsciiConverter : Controller() {
    val initialContent =
        "I gave a cry of astonishment. I saw and thought nothing of the other four Martian monsters; my attention was riveted upon the nearer incident.".encodeUtf8()
    val content = initialContent.toProperty()

    fun subscribeEvents() {
        subscribe<ConvertEvent> { event ->
            println("event: $event")
            val nospaces = event.input.replace(" ", "")
            val newValue = when (event.conversion) {
                AsciiConversion.UTF8 -> event.input.encodeUtf8()
                AsciiConversion.Hexadecimal -> nospaces.decodeHex()
                AsciiConversion.Base64 -> event.input.decodeBase64()
                AsciiConversion.Binary -> event.input.fromBase(2)
                AsciiConversion.Decimal -> event.input.fromBase(10)
                else -> error("Can not convert back from $event")
            }!!
            content.set(newValue)
            fire(NewConversion)
        }
        subscribe<ClipboardEvent> { e ->
            val text = convert(e.conversion)
            clipboard.setContent {
                putString(text)
            } 
            println(text)
        }
    }

    fun convert(conversion: AsciiConversion): String {
        val value = content.value
        return when (conversion) {
            AsciiConversion.UTF8 -> value.utf8()
            AsciiConversion.Hexadecimal -> value.toBase(16)
            AsciiConversion.Base64 -> value.base64()
            AsciiConversion.Binary -> value.toBase(2)
            AsciiConversion.Decimal -> value.toBase(10)
            AsciiConversion.SHA1 -> value.sha1().hex()
            AsciiConversion.MD5 -> value.md5().hex()
        }
    }
}

fun String.fromBase(base: Int): ByteString {
    val buffer = Buffer()
    split(" ").forEach { word ->
        val b = word.toIntOrNull(base)
        if (b != null) buffer.writeByte(b)
    }
    return buffer.readByteString()
}

fun ByteString.toBase(radix: Int): String {
    val b = this
    return buildString {
        for (i in 0 until b.size) {
            append(b[i].toInt().toString(radix))
            append(" ")
        }
    }
}
