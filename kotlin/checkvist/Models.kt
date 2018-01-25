package checkvist

import com.squareup.moshi.Json
import okhttp3.Credentials

data class Group(val name: String, val children: List<Group>? = null)

sealed class PersonTreeItem(open val name: String)
object TreeRoot : PersonTreeItem("Departments")
data class Department(override val name: String): PersonTreeItem(name)
data class Person(override val name: String, val department: String) : PersonTreeItem(name)

val group = Group("Parent",
        listOf(
                Group("Child 1"),
                Group("Child 2"),
                Group("Child 3", listOf(
                        Group("Grand child 3.1",
                                listOf(
                                        Group("Great grandchild 3.1.1"),
                                        Group("Great grandchild 3.1.2"))))
                ),
                Group("Child 4"))
)

val persons = listOf(
        Person("Mary Hanes", "Marketing"),
        Person("Steve Folley", "Customer Service"),
        Person("John Ramsy", "IT Help Desk"),
        Person("Erlick Foyes", "Customer Service"),
        Person("Erin James", "Marketing"),
        Person("Jacob Mays", "IT Help Desk"),
        Person("Larry Cable", "Customer Service"))



interface CheckvistCredentials {
    val defaultList: Int get() = 649516
    val CHECKVIST_KEY: String
    val USER: String
    fun auth() = Credentials.basic(USER, CHECKVIST_KEY)
}


data class CUser(
        val email: String = "",
        val id: Int = 0,
        val username: String = "",
        val pro: Boolean = false,
        val email_md5: String = ""
)

data class CList(
        val id: Int = 0,
        val name: String = "",
        val options: Int = 0,
        val public: Boolean = false,
        val updated_at: String = "",
        @Json(name = "markdown?")
        val markdown: Boolean = false,
        val archived: Boolean = false,
        val read_only: Boolean = false,
        val user_count: Int = 0,
        val user_updated_at: String = "",
        val percent_completed: Double = 0.0,
        val task_count: Int = 0,
        val task_completed: Int = 0,
        val tags: Unknown,
        val tags_as_text: String = ""
)

data class CTask(
        val id: Int = 0,
        val parent_id: Int = 0,
        val checklist_id: Int = 0,
        val status: Int = 0,
        val position: Int = 0,
        val tasks: List<Int> = emptyList(),
        val update_line: String = "",
        val updated_at: String = "",
        val due: String = "",
        val content: String = "",
        val collapsed: Boolean = false,
        val comments_count: Int = 0,
        val assignee_ids: List<Int> = emptyList(),
        val due_user_ids: List<Int> = emptyList(),
        val details: Unknown,
        val tags: Unknown,
        val tags_as_text: String = "",
        val color: Unknown
)

data class CNewTask(
        val parent_id: Int = 0,
        val position: Int? = 1,
        val content: String = ""
)

data class CNote(
        val comment: String = "",
        val created_at: String = "",
        val id: Int = 0,
        val task_id: Int = 0,
        val updated_at: String = "",
        val user_id: Int = 0,
        val username: String = ""
)