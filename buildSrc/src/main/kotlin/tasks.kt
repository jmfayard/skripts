import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer

/**
 * Replacement for groovy's deprecated left shift operator
 * https://docs.gradle.org/3.2/release-notes.html#the-left-shift-operator-on-the-task-interface
 *

```kotlin
val hello1 = tasks.newSimpleTask(name = "hello1", description = "Prints Hello World") {
println("Hello World 1")
}
val hello2 = tasks.newSimpleTask(name = "hello2", dependsOn = listOf(":hello1")) {
println("Hello World 2")
}
```

 */
fun TaskContainer.newSimpleTask(
    name: String,
    description: String = "",
    group: String = "Custom",
    dependsOn: List<String> = emptyList(),
    dependsOnTasks: List<Task> = emptyList(),
    doLast: () -> Unit
): DefaultTask {
    return this.create(name, DefaultTask::class.java) {
        this.group = group
        this.description = description
        this.dependsOn.addAll(dependsOn)
        this.dependsOn.addAll(dependsOnTasks)
        doLast {
            doLast()
        }
    }
}
