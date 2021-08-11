package phonebook

import java.io.File

fun main() {
    val names = File("w:/download/find.txt").readLines()
    val directory = File("w:/download/directory.txt").readLines().map {
        val (number, name) = it.split(" ", limit = 2)
        name to number
    }

    println("Start searching (linear search)...")
    val start = System.currentTimeMillis()
    var count = linearTest(names, directory)
    val elapsed = System.currentTimeMillis() - start
    println("Found $count / ${names.size} entries. Time taken: ${formatElapsedTime(elapsed)}")

    println("Start searching (bubble sort + jump search)...")
    val sortedDirectory = mutableListOf<Pair<String, String>>()
    val startBubbleTestTimestamp = System.currentTimeMillis()
    val searchingTimeStamp: Long
    val res = bubbleSort(directory, sortedDirectory, start, elapsed)
    val sortingTimeStamp = System.currentTimeMillis()
    if (res) {
        count = jumpSearchTest(names, sortedDirectory)
        searchingTimeStamp = System.currentTimeMillis()
    } else {
        count = linearTest(names, directory)
        searchingTimeStamp = System.currentTimeMillis()
    }
    println("Found $count / ${names.size} entries. Time taken: ${formatElapsedTime(searchingTimeStamp - startBubbleTestTimestamp)}")
    println("Sorting time: ${formatElapsedTime(sortingTimeStamp - startBubbleTestTimestamp)} - STOPPED, moved to linear search")
    println("Searching time: ${formatElapsedTime(searchingTimeStamp - sortingTimeStamp)}")
}

fun jumpSearchTest(names: List<String>, directory: MutableList<Pair<String, String>>): Int {
    var count = 0

    for (name in names) {
        if (jumpSearch(name, directory)) {
            count++
        }
    }
    return count
}

fun jumpSearch(name: String, directory: MutableList<Pair<String, String>>): Boolean {
    val size = directory.size
    val blockSize = kotlin.math.floor(kotlin.math.sqrt(size.toDouble())).toInt()

    var step = blockSize
    var prev = 0
    while (directory[kotlin.math.min(step, size) - 1].first < name) {
        prev = step
        step += blockSize
        if (prev >= size) {
            return false
        }
    }

    while (directory[prev].first < name) {
        prev++
        if (prev == kotlin.math.min(step, size)) {
            return false
        }
    }

    if (directory[prev].first == name) {
        return true
    }

    return false
}

private fun linearTest(names: List<String>, directory: List<Pair<String, String>>): Int {
    var count = 0
    for (name in names) {
        if (linearSearch(name, directory).isNotEmpty()) {
            count++
        }
    }
    return count
}

private fun formatElapsedTime(milliseconds: Long): String {
    return String.format(String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", milliseconds))
}

fun linearSearch(name: String, directory: List<Pair<String, String>>): String {
    for (n in directory) {
        if (n.first == name) {
            return n.second
        }
    }
    return ""
}

fun bubbleSort(list: List<Pair<String, String>>,
               result: MutableList<Pair<String, String>>,
               timeStart: Long,
               timeLimit: Long): Boolean {
    result.addAll(list)
    for (i in 0..result.lastIndex) {
        for (j in 0..result.lastIndex - i) {
            val a = result[i]
            val b = result[j]
            if (a.first > b.first) {
                val tmp = result[i]
                result[i] = result[j]
                result[j] = tmp
            }
            val elapsed = System.currentTimeMillis() - timeStart
            if (elapsed > timeLimit) {
                return false
            }
        }
    }
    return true
}
