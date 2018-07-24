

#### Based on Kotlin Scripting Kickstarter

Kotlin Scripting Kickstarter demonstrates how [Kotlin](http://kotlin.jetbrains.org/) can be used as a scripting language.

See https://github.com/andrewoma/kotlin-scripting-kickstarter

#### Kickstarter Features

* Open the project in IntelliJ and develop scripts with full IDE support (completion, compilation, testing and debugging).
* Run scripts as standard unix scripts on the command line. e.g. `#!/usr/bin/env kotlin-script.sh` 
* Edit scripts using any editor and they will be automatically compiled and cached when run.
* Use gradle to include libraries available in maven repositories in your scripts.
* Automatically bootstrap the environment. Just run any script and the Kotlin runitme and library dependencies
  will be downloaded automatically.

#### Quick Start

All you need to get going is a JDK installed and a unix-ish environment.

```shell
$ ./gradlew assemble copyToLib
$ export PATH=$PATH:`pwd`/kotlin
```

## Sample script

`$ cat kotlin/helloworld.kt `

```kotlin
#!/usr/bin/env kotlin-script.sh
package helloworld

fun main(args: Array<String>) {
    println("Hello World!")
}
```

``` 
$ chmod a+x kotlin/helloworld.kt
$ helloworld.kt
Hello World!
``