import kscript.text.print

/** https://github.com/holgerbrandl/kscript **/


fun parseGradleDependencies(lines: List<String>) {
    lines
            .filter { "^de0[-0]*".toRegex().matches(it) }
            .map { it + "foo:" }
            .print()
}

