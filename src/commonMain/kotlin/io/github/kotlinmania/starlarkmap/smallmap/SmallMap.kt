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
import io.github.kotlinmania.starlarkmap.vecmap.VecMap
import io.github.kotlinmania.starlarkmap.vecmap.sortKeys as vecMapSortKeys

/**
 * A map with deterministic iteration order.
 */
class SmallMap<K, V> internal constructor(
    internal val entries: VecMap<K, V>,
) {
    /** Get the entry (occupied or not) for a hashed key. */
    fun entryHashed(key: Hashed<K>): Entry<K, V> {
        val index = getIndexOfHashedByValue(key)
        return if (index != null) {
            Entry.Occupied(OccupiedEntry(this, index))
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
        fun <K, V> withCapacity(n: Int): SmallMap<K, V> = SmallMap(VecMap.withCapacity(n))

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

    fun maybeDropIndex() {
        // No-op in this Kotlin implementation.
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
    }

    fun capacity(): Int = entries.capacity()

    fun first(): Pair<K, V>? = if (entries.isEmpty()) null else entries.getIndex(0)

    fun last(): Pair<K, V>? =
        if (entries.isEmpty()) null else entries.getIndex(entries.len() - 1)

    fun isEmpty(): Boolean = entries.isEmpty()

    fun len(): Int = entries.len()

    fun clear() {
        entries.clear()
    }

    fun getIndex(index: Int): Pair<K, V>? = entries.getIndex(index)

    fun getHashedByValue(key: Hashed<K>): V? {
        val index = getIndexOfHashedByValue(key) ?: return null
        return entries.valueAt(index)
    }

    fun <Q> getHashed(key: Hashed<Q>): V? where Q : Equivalent<K> {
        val index = getIndexOfHashed(key) ?: return null
        return entries.valueAt(index)
    }

    fun get(key: K): V? {
        val index = getIndexOf(key) ?: return null
        return entries.valueAt(index)
    }

    fun <Q> get(key: Q): V? where Q : Equivalent<K> {
        val index = getIndexOf(key) ?: return null
        return entries.valueAt(index)
    }

    fun getIndexOfHashedByValue(key: Hashed<K>): Int? {
        return entries.getIndexOfHashedRaw(key.hash()) { k -> k == key.key() }
    }

    fun <Q> getIndexOfHashed(key: Hashed<Q>): Int? where Q : Equivalent<K> {
        return entries.getIndexOfHashed(key)
    }

    fun getIndexOf(key: K): Int? {
        for (i in 0 until entries.len()) {
            if (entries.keyAt(i) == key) return i
        }
        return null
    }

    fun <Q> getIndexOf(key: Q): Int? where Q : Equivalent<K> {
        for (i in 0 until entries.len()) {
            if (key.equivalent(entries.keyAt(i))) return i
        }
        return null
    }

    fun insertHashedUniqueUnchecked(key: Hashed<K>, value: V) {
        entries.insertHashedUniqueUnchecked(key, value)
    }

    fun insertHashed(key: Hashed<K>, value: V): V? {
        val index = getIndexOfHashedByValue(key)
        return if (index != null) {
            val prev = entries.valueAt(index)
            entries.setValue(index, value)
            prev
        } else {
            entries.insertHashedUniqueUnchecked(key, value)
            null
        }
    }

    fun insert(key: K, value: V): V? {
        return insertHashed(Hashed.new(key), value)
    }

    fun insertUniqueUnchecked(key: K, value: V): Pair<K, V> {
        val hashed = Hashed.new(key)
        entries.insertHashedUniqueUnchecked(hashed, value)
        return Pair(hashed.key(), value)
    }

    fun shiftRemoveHashedByValue(key: Hashed<K>): V? {
        val index = getIndexOfHashedByValue(key) ?: return null
        val (_, v) = entries.remove(index)
        return v
    }

    fun <Q> shiftRemoveHashed(key: Hashed<Q>): V? where Q : Equivalent<K> {
        val index = getIndexOfHashed(key) ?: return null
        val (_, v) = entries.remove(index)
        return v
    }

    fun <Q> shiftRemoveHashedEntry(key: Hashed<Q>): Pair<K, V>? where Q : Equivalent<K> {
        val index = getIndexOfHashed(key) ?: return null
        val (h, v) = entries.remove(index)
        return Pair(h.intoKey(), v)
    }

    fun shiftRemoveIndexHashed(i: Int): Pair<Hashed<K>, V>? {
        if (i < 0 || i >= entries.len()) return null
        return entries.remove(i)
    }

    fun shiftRemoveIndex(i: Int): Pair<K, V>? {
        val (key, value) = shiftRemoveIndexHashed(i) ?: return null
        return Pair(key.intoKey(), value)
    }

    fun shiftRemove(key: K): V? {
        val index = getIndexOf(key) ?: return null
        val (_, v) = entries.remove(index)
        return v
    }

    fun <Q> shiftRemove(key: Q): V? where Q : Equivalent<K> {
        val index = getIndexOf(key) ?: return null
        val (_, v) = entries.remove(index)
        return v
    }

    fun shiftRemoveEntry(key: K): Pair<K, V>? {
        val index = getIndexOf(key) ?: return null
        val (h, v) = entries.remove(index)
        return Pair(h.intoKey(), v)
    }

    fun <Q> shiftRemoveEntry(key: Q): Pair<K, V>? where Q : Equivalent<K> {
        val index = getIndexOf(key) ?: return null
        val (h, v) = entries.remove(index)
        return Pair(h.intoKey(), v)
    }

    fun pop(): Pair<K, V>? {
        val (h, v) = entries.pop() ?: return null
        return Pair(h.intoKey(), v)
    }

    fun stateCheck() {
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
    }

    /** Retains only the elements specified by the predicate. */
    fun retain(f: (K, V) -> Boolean) {
        entries.retain(f)
    }

    fun extend(iter: Iterable<Pair<K, V>>) {
        for ((key, value) in iter) {
            insert(key, value)
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
}
