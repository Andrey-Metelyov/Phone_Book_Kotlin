package phonebook

class HashTable<E>  {
    var size = 0
    private var buckets = MutableList<MutableList<E>>(8) { mutableListOf<E>() }

    companion object {
        fun <E> create(collection: Collection<E>): HashTable<E> {
            val h = HashTable<E>()
            for (e in collection) {
                h.add(e)
            }
            return h
        }

        const val maxLoadFactor = 0.75F
    }

    fun find(value: E, comparator: (E, E) -> Boolean): Boolean {
        val hash = kotlin.math.abs(value.hashCode())
        val bucket = hash % buckets.size
        if (buckets[bucket].size > 0) {
            for (e in buckets[bucket]) {
                if (comparator(e, value)) {
                    return true
                }
            }
        }
        return false
    }

    fun add(e: E) {
        val hash = kotlin.math.abs(e.hashCode())
        val bucket = hash % buckets.size
        buckets[bucket].add(e)
        size++
        if (size.toFloat() / buckets.size > maxLoadFactor) {
            rearrange()
        }
    }

    private fun rearrange() {
        val newBuckets = MutableList<MutableList<E>>(buckets.size * 2) { mutableListOf<E>() }
        val elements = buckets.flatMap { it }
        for (e in elements) {
            val hash = kotlin.math.abs(e.hashCode())
            val bucket = hash % newBuckets.size
            newBuckets[bucket].add(e)
        }
        buckets = newBuckets
    }
}
