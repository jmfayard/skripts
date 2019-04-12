package adventofcode2018

import java.io.File

fun aocReadInput(path: String) : List<String> {
    val resources = "/Users/jmfayard/Dev/skripts/resources/aoc"
    return File(resources, path).readText().lines()
}
