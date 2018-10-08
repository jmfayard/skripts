package tornado

import javafx.scene.layout.GridPane
import tornadofx.*

class OkApp: App(DemoTableView::class, OkStyles::class)

class OkStyles : Stylesheet() {
    companion object {
        val list by cssclass()
    }

    init {
        select(list) {
            padding = box(15.px)
            vgap = 7.px
            hgap = 10.px
        }
    }
}


class DemoTableView : View() {
    override val root = GridPane()

    val mapTableContent = mapOf(Pair("item 1", 5), Pair("item 2", 10), Pair("item 3", 6))

    init {
        with (root) {
            row {
                vbox {
                    label("Tableview from a map")
//                    tableview(FXCollections.observableArrayList<Map.Entry<String, Int>>(mapTableContent.entries)) {
//                        column("Item", Map.Entry<String, Int>::key)
//                        column("Count", Map.Entry<String, Int>::value)
//                        resizeColumnsToFitContent()
//                    }
                }
            }
        }
    }

}