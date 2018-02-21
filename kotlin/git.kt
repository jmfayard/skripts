#!/usr/bin/env kotlin-script.sh
package git

import debug
import jmfayard.executeBashCommand


private val DEFAULT = "help"
private fun usage(): Nothing {
  println(
    """
$ git.kt COMMAND OPTIONS

Git wrappers

COMMAND can be
    help            -> print usage
    branch          -> mass delete local branches
    ignore          -> ignored files
    """.trim()
  )
  System.exit(1)
  error("")
}


fun main(args: Array<String>) {
  when (args.firstOrNull() ?: DEFAULT) {
    "help" -> usage()
    "branch" -> gitLocalBranches()
  }

}


fun gitLocalBranches() {
  val reservedBranches = listOf("master", "production", "fudgepack", "development", "offline")
  val git = "git"
  val origins = executeBashCommand(git, "remote").lines().filter { it.isNotBlank() }.debug("origins")
  for (origin in origins) {
    executeBashCommand(git, "remote", "prune", origin).debug("prune")
  }

  val branches = executeBashCommand(git, "branch", "-vv")
    .lines()
    .filter { it.startsWith("  ") }
    .map { line ->
      line.substring(2).split(" ", limit = 2)
    }.filterNot { list -> list.first() in reservedBranches }

  for (list in branches) {
    val (branch, rest) = list
    println("git branch -D '$branch' # $rest")
  }
}




