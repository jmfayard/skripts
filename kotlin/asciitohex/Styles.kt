package asciitohex

import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val buttons by cssclass()
        val title by cssclass()
    }

    init {
        s(title) {
            fontSize = 24.px
            padding = box(16.px, 48.px)
            
        }
        s(buttons) {
            padding = box(0.px, 16.px, 24.px, 16.px)
        }
    }
}
