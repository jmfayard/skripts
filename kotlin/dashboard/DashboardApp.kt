package dashboard

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import javafx.scene.control.TextArea
import org.controlsfx.control.Notifications
import tornadofx.App
import tornadofx.Controller
import tornadofx.ItemViewModel
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.Workspace
import tornadofx.action
import tornadofx.addClass
import tornadofx.box
import tornadofx.button
import tornadofx.cssclass
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.item
import tornadofx.launch
import tornadofx.listview
import tornadofx.menu
import tornadofx.menubar
import tornadofx.observable
import tornadofx.onUserSelect
import tornadofx.px
import tornadofx.required
import tornadofx.textarea
import tornadofx.textfield
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.nio.charset.Charset
import java.util.Arrays

fun main(args: Array<String>) {
    launch<DashboardApp>(args)
}

class DashboardApp : App(DashboardView::class, DashboardStyles::class)

class MainController : Controller() {

    val views = mapOf(
        "New Account" to AccountForm(),
        "Edit Account" to AccountForm(),
        "Auth Token" to CategoryListView(),
        "Accounts" to CategoryListView(),
        "SmartCards" to CategoryListView()
    )
}

class DashboardView : Workspace("Patronus") {
    val controller = MainController()

    init {
        menubar {
            menu("Views") {
                for ((viewName, viewNode) in controller.views) {
                    item(viewName) {
                        action {
                            workspace.dock(viewNode)
                        }
                    }
                }
            }
            with(bottomDrawer) {
                item("Logs") {
                    textarea {
                        addClass("consola")
                        val ps = PrintStream(TextAreaOutputStream(this))
                        System.setErr(ps)
                        System.setOut(ps)
                    }
                }
            }
        }
    }
}

class CategoryListView : View() {

    override val root: Parent = listview<String> {
        items = listOf("1", "2", "3").observable()
        prefWidth = 150.0
        cellFormat { text = it }
        onUserSelect {
            println(it)
        }
    }
}

class DashboardStyles : Stylesheet() {
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

data class Account(
    val accountId: String = "",
    val accountName: String = "",
    val active: Boolean = false,
    val activeCard: Boolean = false,
    val role: String = "",
    val firstName: String = "",
    val middleNames: String = "",
    val lastName: String = "",
    val suffix: String = "",
    val preferredName: String = "",
    val gender: String = "",
    val entityName: String = ""
)

class AccountModel : ItemViewModel<Account>(Account()) {

    val accountId = bind { SimpleStringProperty(item?.accountId ?: "") }
    val phoneNumber = bind { SimpleStringProperty(item?.accountId ?: "") }
    val firstName = bind { SimpleStringProperty(item?.accountId ?: "") }
    val lastName = bind { SimpleStringProperty(item?.accountId ?: "") }
}

class AccountForm : View("Account") {
    val model: AccountModel by inject()

    override val root = form {
        fieldset("Personal Information", FontAwesomeIconView(FontAwesomeIcon.USER)) {
            field("First Name") {
                textfield(model.firstName).required()
            }

            field("Last Name") {
                textfield(model.lastName).required()
            }
            field("Phone Number") {
                textfield(model.phoneNumber).required()
            }

            field("AccountId") {
                textfield(model.accountId).required()
            }
        }

        button("Save") {
            action {
                model.commit {
                    val customer = model.item
                    Notifications.create()
                        .title("Customer saved!")
                        .text(customer.toString())
                        .owner(this)
                        .showInformation()
                }
            }

//            enableWhen(model.valid)
        }
    }
}

class TextAreaOutputStream(val textArea: TextArea) : OutputStream() {

    /**
     * This doesn't support multibyte characters streams like utf8
     */
    @Throws(IOException::class)
    override fun write(b: Int) {
        throw UnsupportedOperationException()
    }

    /**
     * Supports multibyte characters by converting the array buffer to String
     */
    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        // redirects data to the text area
        textArea.appendText(String(Arrays.copyOf(b, len), Charset.defaultCharset()))
        // scrolls the text area to the end of data
        textArea.scrollTop = java.lang.Double.MAX_VALUE
    }
}
