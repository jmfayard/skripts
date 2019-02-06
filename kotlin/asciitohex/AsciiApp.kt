package asciitohex

import tornadofx.App
import tornadofx.launch

fun main() {
    launch<AsciiApp>()
}

class AsciiApp : App(AsciiView::class, Styles::class)

