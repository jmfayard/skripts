package asciitohex

import tornadofx.FXEvent

data class ConvertEvent(
    val conversion: AsciiConversion,
    val input: String
) : FXEvent()

data class ClipboardEvent(val conversion: AsciiConversion) : FXEvent()
object NewConversion : FXEvent()
