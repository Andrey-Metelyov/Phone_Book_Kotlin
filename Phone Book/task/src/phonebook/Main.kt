package phonebook

import java.io.File

fun main() {
    val names = File("w:/download/find.txt").readLines()
    val directory = File("w:/download/directory.txt").readLines().map {
        val (number, name) = it.split(" ", limit = 2)
        name to number
    }.toMap()

    println("Start searching...")
    val start = System.currentTimeMillis()
    var count = 0
    for (name in names) {
        if (linearSearch(name, directory).isNotEmpty()) {
            count++
        }
    }
    val elapsed = System.currentTimeMillis() - start
    val timeTaken = String.format(String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", elapsed))
    println("Found $count / ${names.size} entries. Time taken: $timeTaken")
}

fun linearSearch(name: String, directory: Map<String, String>): String {
    for (n in directory.keys) {
        if (n == name) {
            return directory[n]!!
        }
    }
    return ""
}