import io.kotlintest.specs.StringSpec
import kotlinx.collections.immutable.*

class ImmutableTest : StringSpec() { init {

    val initialState = Store(todos = immutableListOf())
    val events = immutableListOf<Event>(
        AddTodo("Buy grocery", 1),
        AddTodo("Cook foode", 2),
        EditTodo("Cook Food", 2),
        AddTodo("Call Mom", 3),
        ToggleCompletedTodo(3),
        ToggleCompletedTodo(2),
        ToggleCompletedTodo(2),
        RemoveTodo(2),
        DeleteCompleted
    )

    val states = redux(initialState, events, ::todoReducer)

    "Size" {
        states.size shouldBe (events.size + 1)
    }
    "Adding" {
        val first = Todo(1, "Buy grocery", false)
        states[1].todos shouldBe immutableListOf(first)
    }
    "Editing" {
        states[3].todo(2)!!.text shouldBe "Cook Food"
    }
    "Toggle" {
        states[8].completed() shouldBe 1
    }
    "Final state" {
        val first = Todo(1, "Buy grocery", false)
        states.last().todos shouldBe immutableListOf(first)
    }
}}

fun <S, E> redux(state : S, events: ImmutableList<E>, reducer: (S, E) -> S) : ImmutableList<S> {
    var current = state
    println(current)
    var list = immutableListOf(state)
    for (e in events) {
        current = reducer(current, e)
        println(current)
        list += current
    }
    return list
}

data class Store(val todos : ImmutableList<Todo>) {
    fun todo(id: Int) : Todo? =  todos.firstOrNull { it.id == id }
    fun completed() : Int = todos.filter(Todo::completed).count()
}

fun todoReducer(store: Store, event: Event): Store {
    val todos = when (event) {
        is AddTodo -> store.todos + Todo(event.id, event.text, false)
        is RemoveTodo -> store.todos.filterNot { it.id == event.id }.toImmutableList()
        DeleteCompleted -> store.todos.filter { !it.completed }.toImmutableList()
        is ToggleCompletedTodo -> {
            val todo = store.todo(event.id)
            if (todo == null) {
                store.todos
            } else {
                store.todos - todo + todo.copy(completed = !todo.completed)
            }
        }
        is EditTodo -> {
            val todo = store.todo(event.id)
            if (todo == null) {
                store.todos
            } else {
                store.todos - todo + todo.copy(text = event.text)
            }
        }
    }
    if (todos == store.todos) {
        println("Warning: no modification for event $event")
        return store
    } else {
        return Store(todos)
    }
}


data class Todo(val id: Int, val text: String, val completed: Boolean)

sealed class Event
data class AddTodo(val text : String, val id: Int = generateId()) : Event()
data class RemoveTodo(val id: Int) : Event()
data class ToggleCompletedTodo(val id: Int) : Event()
object DeleteCompleted : Event()
data class EditTodo(val text: String, val id: Int) : Event()


private var maxId : Int = 0
fun generateId() = maxId++