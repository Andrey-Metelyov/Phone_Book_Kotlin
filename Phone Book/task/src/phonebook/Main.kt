package phonebook

import java.io.File

fun main() {
    val names = File("w:/download/find.txt").readLines()
//    names.addAll(names)
    val directory = File("w:/download/directory.txt").readLines().map {
        val (number, name) = it.split(" ", limit = 2)
        name to number
    }
    var startTestTimestamp = 0L
    var sortingTimeStamp = 0L
    var searchingTimeStamp = 0L
    var count = 0

    println("Start searching (linear search)...")
    val start = System.currentTimeMillis()
    count = searchTest(names, directory, ::linearSearch)
    val elapsed = System.currentTimeMillis() - start
    println("Found $count / ${names.size} entries. Time taken: ${formatElapsedTime(elapsed)}")

    println()
    println("Start searching (bubble sort + jump search)...")
    val sortedDirectory = mutableListOf<Pair<String, String>>()
    startTestTimestamp = System.currentTimeMillis()
    var res = bubbleSort(directory, sortedDirectory, start, elapsed)
    sortingTimeStamp = System.currentTimeMillis()
    if (res) {
        count = searchTest(names, sortedDirectory, ::jumpSearch)
        searchingTimeStamp = System.currentTimeMillis()
    } else {
        count = searchTest(names, directory, ::linearSearch)
        searchingTimeStamp = System.currentTimeMillis()
    }
    println("Found $count / ${names.size} entries. Time taken: ${formatElapsedTime(searchingTimeStamp - startTestTimestamp)}")
    println("Sorting time: ${formatElapsedTime(sortingTimeStamp - startTestTimestamp)}" + if (!res) " - STOPPED, moved to linear search" else "")
    println("Searching time: ${formatElapsedTime(searchingTimeStamp - sortingTimeStamp)}")

    sortedDirectory.clear()
    println()
    println("Start searching (quick sort + binary search)...")
    startTestTimestamp = System.currentTimeMillis()
    res = quickSort(directory, sortedDirectory, start, elapsed)
    sortingTimeStamp = System.currentTimeMillis()
    if (res) {
        count = searchTest(names, sortedDirectory, ::binarySearch)
        searchingTimeStamp = System.currentTimeMillis()
    } else {
        count = searchTest(names, directory, ::linearSearch)
        searchingTimeStamp = System.currentTimeMillis()
    }
    println("Found $count / ${names.size} entries. Time taken: ${formatElapsedTime(searchingTimeStamp - startTestTimestamp)}")
    println("Sorting time: ${formatElapsedTime(sortingTimeStamp - startTestTimestamp)}" + if (!res) " - STOPPED, moved to linear search" else "")
    println("Searching time: ${formatElapsedTime(searchingTimeStamp - sortingTimeStamp)}")

    println()
    println("Start searching (hash table)...")
    startTestTimestamp = System.currentTimeMillis()
    val ht = HashTable.create(directory.map { it.first })
    sortingTimeStamp = System.currentTimeMillis()
    // { (e1, e2) -> e1.first == e2.first }
    count = searchHTTest(names, ht) // , { (e1, e2) -> { e1.first == e2.first } }
    searchingTimeStamp = System.currentTimeMillis()
    println("Found $count / ${names.size} entries. Time taken: ${formatElapsedTime(searchingTimeStamp - startTestTimestamp)}")
    println("Creating time: ${formatElapsedTime(sortingTimeStamp - startTestTimestamp)}")
    println("Searching time: ${formatElapsedTime(searchingTimeStamp - sortingTimeStamp)}")
}

fun searchHTTest(names: List<String>,
                 hashTable: HashTable<String>): Int {
    // , comparator: (Pair<String, String>, Pair<String, String>) -> Boolean
    var count = 0
    for (name in names) {
        if (hashTable.find(name, { e1, e2 -> e1 == e2 })) {
            count++
        }
    }
    return count
}

fun searchTest(names: List<String>, directory: List<Pair<String, String>>, algorithm: (String, List<Pair<String, String>>) -> Boolean): Int {
    var count = 0
    for (name in names) {
        if (algorithm(name, directory)) {
            count++
        }
    }
    return count
}

//fun hashTableSearch(name, directory: List<Pair<String, String>>)

fun quickSort(list: List<Pair<String, String>>, sortedList: MutableList<Pair<String, String>>, start: Long, elapsed: Long): Boolean {
    sortedList.addAll(list)
    qs(sortedList, 0, sortedList.lastIndex)
    return true
}

fun qs(list: MutableList<Pair<String, String>>, low: Int, high: Int) {
    if (low < high) {
        val p = partition(list, low, high)
        qs(list, low, p - 1)
        qs(list, p + 1, high)
    }
}

fun partition(list: MutableList<Pair<String, String>>, low: Int, high: Int): Int {
    val pivot = list[high].first
    var i = low
    for (j in low until high) {
        if (list[j].first <= pivot) {
            list[i] = list[j].also { list[j] = list[i] }
            i++
        }
    }
    list[i] = list[high].also { list[high] = list[i] }
    return i
}

fun jumpSearch(name: String, directory: List<Pair<String, String>>): Boolean {
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

private fun formatElapsedTime(milliseconds: Long): String {
    return String.format(String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", milliseconds))
}

fun linearSearch(name: String, directory: List<Pair<String, String>>): Boolean {
    for (n in directory) {
        if (n.first == name) {
            return true
        }
    }
    return false
}

fun binarySearch(name: String, directory: List<Pair<String, String>>): Boolean {
    var left = 0
    var right = directory.lastIndex

    while (left <= right) {
        val mid = (left + right) / 2
        if (directory[mid].first < name) {
            left = mid + 1
        } else if (directory[mid].first > name) {
            right = mid - 1
        } else {
            return true
        }
    }
    return false
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
