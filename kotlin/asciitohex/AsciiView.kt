package asciitohex

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.button
import tornadofx.fieldset
import tornadofx.form
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.textarea
import tornadofx.useMaxWidth
import tornadofx.vbox

class AsciiView : View() {
    val converter: AsciiConverter by inject()
    val areas = mutableMapOf<AsciiConversion, TextArea>()
    val labels = mutableMapOf<AsciiConversion, Label>()

    init {
        subscribe<NewConversion> { event ->
            println("Event $event areas: ${areas.keys}")
            for (conversion in AsciiConversion.values()) {
                val display = converter.convert(conversion)
                if (conversion.readOnly()) labels[conversion]!!.text = display
                else areas[conversion]!!.text = display
            }
        }
    }

    override val root = borderpane {
        prefHeight = 600.0
        title = "ASCII to Hex"
        top = hbox {
            addClass(Styles.title)
            label("ASCII to Hex")
            label("...and other text conversion tools")
        }
        center = convertView()

        println(converter.content.get())
        converter.subscribeEvents()
        runAsync { }.ui { fire(NewConversion) }

    }

    fun convertView() = form {
        for (conversion in AsciiConversion.values()) {
            hbox {
                fieldset(conversion.name) {
                    useMaxWidth = true
                    if (conversion.readOnly()) {
                        labels += conversion to label("") {
                            hgrow = Priority.ALWAYS
                        }
                    } else {
                        val area = textarea("") {
                            isWrapText = true
                        }
                        areas += conversion to area
                    }

                }
                vbox(spacing = 16, alignment = Pos.BOTTOM_CENTER) {
                    addClass(Styles.buttons)
                    button("Copy") {
                        useMaxWidth = conversion.readOnly().not()
                        action {
                            fire(ClipboardEvent(conversion))
                        }
                    }
                    if (conversion.readOnly().not()) button("Convert") {
                        useMaxWidth = true
                        action {
                            val event = ConvertEvent(conversion, areas[conversion]!!.text)
                            println("Action Convert for $conversion <= $event")
                            fire(event)
                        }
                    }
                }
            }
        }

    }
}
