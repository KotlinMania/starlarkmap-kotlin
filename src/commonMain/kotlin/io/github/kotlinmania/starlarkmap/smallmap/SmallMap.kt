// port-lint: source small_map.rs
package io.github.kotlinmania.starlarkmap.smallmap

/*
 * Copyright 2019 The Starlark in Rust Authors.
 * Copyright (c) Facebook, Inc. and its affiliates.
 * Copyright (c) 2025 Sydney Renee, The Solace Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not import this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.github.kotlinmania.starlarkmap.Equivalent
import io.github.kotlinmania.starlarkmap.Hashed
import io.github.kotlinmania.starlarkmap.StarlarkHashValue
import io.github.kotlinmania.starlarkmap.vecmap.VecMap
import io.github.kotlinmania.starlarkmap.vecmap.sortKeys as vecMapSortKeys

/** Max size of a map when we do not create an index. */
private const val NO_INDEX_THRESHOLD: Int = 32

/**
 * A map with deterministic iteration order.
 */
class SmallMap<K, V> internal constructor(
    internal val entries: VecMap<K, V>,
    /**
     * Map a hash to the index in `entries`. This field is initialized when the size of the
     * map exceeds [NO_INDEX_THRESHOLD]. Each hash is mapped to a list of indices to handle
     * collisions.
     */
    internal var index: HashMap<StarlarkHashValue, MutableList<Int>>? = null,
) {
    /** Get the entry (occupied or not) for a hashed key. */
    fun entryHashed(key: Hashed<K>): Entry<K, V> {
        val i = getIndexOfHashedByValue(key)
        return if (i != null) {
            Entry.Occupied(OccupiedEntry(this, i))
        } else {
            Entry.Vacant(VacantEntry(this, key))
        }
    }

    /** Get the entry (occupied or not) for a key. */
    fun entry(key: K): Entry<K, V> = entryHashed(Hashed.new(key))

    companion object {
        /**
         * Empty map.
         */
        fun <K, V> new(): SmallMap<K, V> = SmallMap(VecMap.new())

        fun <K, V> default(): SmallMap<K, V> = new()

        /**
         * Create an empty map with specified capacity.
         */
        fun <K, V> withCapacity(n: Int): SmallMap<K, V> {
            val map = SmallMap<K, V>(VecMap.withCapacity(n))
            if (n > NO_INDEX_THRESHOLD) {
                map.index = HashMap(n)
            }
            return map
        }

        fun <K, V> fromIterator(iter: Iterable<Pair<K, V>>): SmallMap<K, V> {
            val map = withCapacity<K, V>(if (iter is Collection<*>) iter.size else 0)
            for ((key, value) in iter) {
                map.insert(key, value)
            }
            return map
        }

        fun <K, V> fromIter(iter: Iterable<Pair<K, V>>): SmallMap<K, V> = fromIterator(iter)
    }

    override fun toString(): String =
        (0 until entries.len()).joinToString(prefix = "{", postfix = "}", separator = ", ") { i ->
            "${formatDebug(entries.keyAt(i))}: ${formatDebug(entries.valueAt(i))}"
        }

    /**
     * Drop the index if the map is too small, and the index is not really needed.
     *
     * We don't allocate index prematurely when we add entries the map, but we keep it
     * allocated when we remove entries from the map.
     */
    fun maybeDropIndex() {
        if (entries.len() <= NO_INDEX_THRESHOLD) {
            index = null
        }
    }

    fun keys(): Sequence<K> = entries.keys()

    fun values(): Sequence<V> = entries.values()

    fun valuesMut(): Sequence<V> = values()

    fun iter(): Sequence<Pair<K, V>> = entries.iter()

    fun iterMut(): Sequence<Pair<K, V>> = iter()

    fun iterMutUnchecked(): Sequence<Pair<K, V>> = iter()

    operator fun iterator(): Iterator<Pair<K, V>> = iter().iterator()

    fun intoIter(): Iterator<Pair<K, V>> = iterator()

    fun intoIterHashed(): Sequence<Pair<Hashed<K>, V>> = iterHashed()

    fun iterHashed(): Sequence<Pair<Hashed<K>, V>> = entries.iterHashed()

    fun reserve(additional: Int) {
        entries.reserve(additional)
        val idx = index
        if (idx != null) {
            // No-op for HashMap — Kotlin's stdlib HashMap doesn't expose reserve.
        } else if (entries.len() + additional > NO_INDEX_THRESHOLD) {
            createIndex(entries.len() + additional)
        }
    }

    fun capacity(): Int = entries.capacity()

    fun first(): Pair<K, V>? = if (entries.isEmpty()) null else entries.getIndex(0)

    fun last(): Pair<K, V>? =
        if (entries.isEmpty()) null else entries.getIndex(entries.len() - 1)

    fun isEmpty(): Boolean = entries.isEmpty()

    fun len(): Int = entries.len()

    fun clear() {
        entries.clear()
        index?.clear()
    }

    fun getIndex(index: Int): Pair<K, V>? = entries.getIndex(index)

    fun getHashedByValue(key: Hashed<K>): V? {
        val i = getIndexOfHashedByValue(key) ?: return null
        return entries.valueAt(i)
    }

    fun <Q> getHashed(key: Hashed<Q>): V? where Q : Equivalent<K> {
        val i = getIndexOfHashed(key) ?: return null
        return entries.valueAt(i)
    }

    fun get(key: K): V? {
        val i = getIndexOf(key) ?: return null
        return entries.valueAt(i)
    }

    fun <Q> get(key: Q): V? where Q : Equivalent<K> {
        val i = getIndexOf(key) ?: return null
        return entries.valueAt(i)
    }

    fun getIndexOfHashedByValue(key: Hashed<K>): Int? {
        return getIndexOfHashedRaw(key.hash()) { k -> k == key.key() }
    }

    fun <Q> getIndexOfHashed(key: Hashed<Q>): Int? where Q : Equivalent<K> {
        return getIndexOfHashedRaw(key.hash()) { k -> key.key().equivalent(k) }
    }

    fun getIndexOf(key: K): Int? =
        getIndexOfHashedRaw(StarlarkHashValue.new(key)) { k -> k == key }

    fun <Q> getIndexOf(key: Q): Int? where Q : Equivalent<K> =
        getIndexOfHashed(Hashed.new(key))

    /**
     * Find the index of an entry by hash + equality predicate.
     *
     * If the index is allocated (large maps), use it for O(1) hash lookup. Otherwise fall
     * back to the underlying [VecMap]'s linear scan. Mirrors small_map.rs:313-323.
     */
    private fun getIndexOfHashedRaw(hash: StarlarkHashValue, eq: (K) -> Boolean): Int? {
        val idx = index
        return if (idx == null) {
            entries.getIndexOfHashedRaw(hash, eq)
        } else {
            val chain = idx[hash] ?: return null
            for (i in chain) {
                if (eq(entries.keyAt(i))) return i
            }
            null
        }
    }

    fun insertHashedUniqueUnchecked(key: Hashed<K>, value: V) {
        val hash = key.hash()
        val entryIndex = entries.len()
        entries.insertHashedUniqueUnchecked(key, value)
        val idx = index
        if (idx != null) {
            idx.getOrPut(hash) { mutableListOf() }.add(entryIndex)
        } else if (entries.len() == NO_INDEX_THRESHOLD + 1) {
            createIndex(entries.len())
        }
    }

    fun insertHashed(key: Hashed<K>, value: V): V? {
        val i = getIndexOfHashedByValue(key)
        return if (i != null) {
            val prev = entries.valueAt(i)
            entries.setValue(i, value)
            prev
        } else {
            insertHashedUniqueUnchecked(key, value)
            null
        }
    }

    fun insert(key: K, value: V): V? = insertHashed(Hashed.new(key), value)

    fun insertUniqueUnchecked(key: K, value: V): Pair<K, V> {
        val hashed = Hashed.new(key)
        insertHashedUniqueUnchecked(hashed, value)
        return Pair(hashed.key(), value)
    }

    fun shiftRemoveHashedByValue(key: Hashed<K>): V? {
        val i = getIndexOfHashedByValue(key) ?: return null
        return removeAtIndex(i).second
    }

    fun <Q> shiftRemoveHashed(key: Hashed<Q>): V? where Q : Equivalent<K> {
        val i = getIndexOfHashed(key) ?: return null
        return removeAtIndex(i).second
    }

    fun <Q> shiftRemoveHashedEntry(key: Hashed<Q>): Pair<K, V>? where Q : Equivalent<K> {
        val i = getIndexOfHashed(key) ?: return null
        val (h, v) = removeAtIndex(i)
        return Pair(h.intoKey(), v)
    }

    fun shiftRemoveIndexHashed(i: Int): Pair<Hashed<K>, V>? {
        if (i < 0 || i >= entries.len()) return null
        return removeAtIndex(i)
    }

    fun shiftRemoveIndex(i: Int): Pair<K, V>? {
        val (h, v) = shiftRemoveIndexHashed(i) ?: return null
        return Pair(h.intoKey(), v)
    }

    fun shiftRemove(key: K): V? {
        val i = getIndexOf(key) ?: return null
        return removeAtIndex(i).second
    }

    fun <Q> shiftRemove(key: Q): V? where Q : Equivalent<K> {
        val i = getIndexOf(key) ?: return null
        return removeAtIndex(i).second
    }

    fun shiftRemoveEntry(key: K): Pair<K, V>? {
        val i = getIndexOf(key) ?: return null
        val (h, v) = removeAtIndex(i)
        return Pair(h.intoKey(), v)
    }

    fun <Q> shiftRemoveEntry(key: Q): Pair<K, V>? where Q : Equivalent<K> {
        val i = getIndexOf(key) ?: return null
        val (h, v) = removeAtIndex(i)
        return Pair(h.intoKey(), v)
    }

    /**
     * Remove the entry at `index`, updating the secondary [index] to drop the removed
     * position and decrement positions above it. Mirrors small_map.rs:565-586.
     */
    private fun removeAtIndex(i: Int): Pair<Hashed<K>, V> {
        val idx = index
        if (idx != null) {
            // Shift positions above `i` down by one and drop the entry pointing at `i`.
            val emptyChains = mutableListOf<StarlarkHashValue>()
            for ((hash, chain) in idx) {
                val it = chain.listIterator()
                while (it.hasNext()) {
                    val j = it.next()
                    when {
                        j == i -> it.remove()
                        j > i -> it.set(j - 1)
                    }
                }
                if (chain.isEmpty()) emptyChains.add(hash)
            }
            for (h in emptyChains) idx.remove(h)
        }
        return entries.remove(i)
    }

    fun pop(): Pair<K, V>? {
        if (entries.isEmpty()) return null
        return shiftRemoveIndex(entries.len() - 1)
    }

    /**
     * Verify that the map is internally consistent. Mirrors small_map.rs:124-141
     * `assert_invariants`.
     */
    fun stateCheck() {
        val idx = index
        if (idx != null) {
            check(idx.values.sumOf { it.size } == entries.len()) {
                "index size mismatch: ${idx.values.sumOf { it.size }} vs ${entries.len()}"
            }
            for (i in 0 until entries.len()) {
                val k = entries.hashedKeyAt(i)
                val chain = idx[k.hash()]
                checkNotNull(chain) { "no index chain for hash ${k.hash()}" }
                check(i in chain) { "index does not list position $i for hash ${k.hash()}" }
            }
        } else {
            check(entries.len() <= NO_INDEX_THRESHOLD) {
                "no index but entries.len()=${entries.len()} > $NO_INDEX_THRESHOLD"
            }
        }
        // Also verify no duplicate keys.
        val seen = HashSet<K>()
        for (i in 0 until entries.len()) {
            check(seen.add(entries.keyAt(i)))
        }
    }

    /** Equal if the keys and values are equal in the iteration order. */
    fun eqOrdered(other: SmallMap<K, V>): Boolean {
        if (len() != other.len()) return false
        val thisIter = iter().iterator()
        val otherIter = other.iter().iterator()
        while (thisIter.hasNext()) {
            if (!otherIter.hasNext()) return false
            if (thisIter.next() != otherIter.next()) return false
        }
        return true
    }

    /** Hash entries in the iteration order. */
    fun hashOrdered(): Int = entries.hashOrdered()

    /** Reverse the iteration order of the map. */
    fun reverse() {
        entries.reverse()
        // Reverse changes every entry's index. Rebuild the index against the new
        // positions. Mirrors small_map.rs:760-769.
        val idx = index
        if (idx != null) {
            val len = entries.len()
            for (chain in idx.values) {
                for (k in chain.indices) {
                    chain[k] = len - 1 - chain[k]
                }
            }
        }
    }

    /** Retains only the elements specified by the predicate. */
    fun retain(f: (K, V) -> Boolean) {
        val originalLen = entries.len()
        entries.retain(f)
        if (entries.len() < originalLen) {
            rebuildIndex()
        }
    }

    fun extend(iter: Iterable<Pair<K, V>>) {
        for ((key, value) in iter) {
            insert(key, value)
        }
    }

    /**
     * Allocate the index and populate it from the current entries.
     * Mirrors small_map.rs:432-443 `create_index`.
     */
    private fun createIndex(capacity: Int) {
        check(index == null)
        check(capacity >= entries.len())
        val idx = HashMap<StarlarkHashValue, MutableList<Int>>(capacity)
        for (i in 0 until entries.len()) {
            val k = entries.hashedKeyAt(i)
            idx.getOrPut(k.hash()) { mutableListOf() }.add(i)
        }
        index = idx
    }

    /**
     * Rebuild the index from the current entries. Used after operations that reorder
     * entries (sort, retain). Mirrors small_map.rs:446-455.
     */
    internal fun rebuildIndex() {
        val idx = index ?: return
        idx.clear()
        for (i in 0 until entries.len()) {
            val k = entries.hashedKeyAt(i)
            idx.getOrPut(k.hash()) { mutableListOf() }.add(i)
        }
    }
}

private fun formatDebug(value: Any?): String = when (value) {
    null -> "null"
    is String -> "\"$value\""
    is Char -> "'$value'"
    else -> value.toString()
}

/** Reference to the actual entry in the map. */
class OccupiedEntry<K, V> internal constructor(
    private val map: SmallMap<K, V>,
    private val index: Int,
) {
    /** Key for this entry. */
    fun key(): K = map.entries.keyAt(index)

    /** Value for this entry. */
    fun get(): V = map.entries.valueAt(index)

    /** Replace the value associated with the entry. */
    fun set(value: V) {
        map.entries.setValue(index, value)
    }
}

/** Reference to a vacant entry in the map. */
class VacantEntry<K, V> internal constructor(
    private val map: SmallMap<K, V>,
    private val keyHashed: Hashed<K>,
) {
    /** Key for this entry. */
    fun key(): K = keyHashed.key()

    /** Insert the value into the entry, returning the inserted value. */
    fun insert(value: V): V {
        map.insertHashedUniqueUnchecked(keyHashed, value)
        return value
    }
}

/** Occupied or vacant entry. */
sealed class Entry<K, V> {
    /** Occupied entry. */
    class Occupied<K, V>(val entry: OccupiedEntry<K, V>) : Entry<K, V>()

    /** No entry for given key. */
    class Vacant<K, V>(val entry: VacantEntry<K, V>) : Entry<K, V>()

    /** Key for this entry. */
    fun key(): K = when (this) {
        is Occupied -> entry.key()
        is Vacant -> entry.key()
    }

    /** Insert if vacant, returning the existing or inserted value. */
    fun orInsert(default: V): V = orInsertWith { default }

    /** Insert if vacant, returning the existing or inserted value. */
    fun orInsertWith(default: () -> V): V = when (this) {
        is Occupied -> entry.get()
        is Vacant -> entry.insert(default())
    }

    /** Modify if present. Returns this entry. */
    fun andModify(f: (V) -> V): Entry<K, V> {
        if (this is Occupied) {
            entry.set(f(entry.get()))
        }
        return this
    }
}

/** Sort entries by key. */
fun <K : Comparable<K>, V> SmallMap<K, V>.sortKeys() {
    entries.vecMapSortKeys()
    rebuildIndex()
}
