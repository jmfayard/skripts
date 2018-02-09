

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
$ date_me.kt
1487239456                           seconds since EPOCH
1487239456368                        milliseconds since EPOCH
2017-02-16                           UTC time (simple format)
2017-02-16T10:04:16.368Z             UTC time
2017-02-16 10:04:16                  UTC time (MySql format)

```
