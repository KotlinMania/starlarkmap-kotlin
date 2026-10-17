// port-lint: source ordered_map.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.starlarkmap.orderedmap

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

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import io.github.kotlinmania.starlarkmap.Equivalent
import io.github.kotlinmania.starlarkmap.Hashed
import io.github.kotlinmania.starlarkmap.StarlarkHasher
import io.github.kotlinmania.starlarkmap.smallmap.Entry
import io.github.kotlinmania.starlarkmap.smallmap.SmallMap
import io.github.kotlinmania.starlarkmap.smallmap.sortKeys
import kotlin.native.HiddenFromObjC

/**
 * Wrapper for [SmallMap] which considers map equal if iteration order is equal.
 *
 * Unlike [SmallMap], two [OrderedMap]s are equal only when they contain the same
 * key-value pairs in the same iteration order.
 */
// generic by design: insertion-ordered map container; key/value params are the public contract.
@HiddenFromObjC
class OrderedMap<K, V> internal constructor(
    internal val inner: SmallMap<K, V>,
) : Iterable<Pair<K, V>>,
    Serialize {
    interface Item

    interface IntoIter

    companion object {
        /** Create a new empty map. */
        fun <K, V> new(): OrderedMap<K, V> = OrderedMap(SmallMap.new())

        /** Create a new empty map with the specified capacity. */
        fun <K, V> withCapacity(capacity: Int): OrderedMap<K, V> =
            OrderedMap(SmallMap.withCapacity(capacity))

        fun <K, V> default(): OrderedMap<K, V> = OrderedMap(SmallMap.new())

        /**
         * Create an [OrderedMap] from an iterable of key-value pairs.
         */
        fun <K, V> fromIterator(iter: Iterable<Pair<K, V>>): OrderedMap<K, V> {
            val map = SmallMap.new<K, V>()
            for ((k, v) in iter) {
                map.insert(k, v)
            }
            return OrderedMap(map)
        }

        fun <K, V> fromIter(iter: Iterable<Pair<K, V>>): OrderedMap<K, V> = fromIterator(iter)

        /**
         * Create an [OrderedMap] from a [SmallMap].
         */
        fun <K, V> from(map: SmallMap<K, V>): OrderedMap<K, V> = OrderedMap(map)

        fun <K, V, D : Deserializer> deserialize(
            deserializer: D,
            keyDeserialize: Deserialize<K>,
            valueDeserialize: Deserialize<V>,
        ): SerdeResult<OrderedMap<K, V>> =
            SmallMap.deserialize(deserializer, keyDeserialize, valueDeserialize).map { OrderedMap(it) }

        fun <K, V> deserialize(
            keyDeserialize: Deserialize<K>,
            valueDeserialize: Deserialize<V>,
        ): Deserialize<OrderedMap<K, V>> =
            object : Deserialize<OrderedMap<K, V>> {
                override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<OrderedMap<K, V>> =
                    OrderedMap.deserialize(deserializer, keyDeserialize, valueDeserialize)
            }
    }

    /** Get the number of elements in the map. */
    fun len(): Int = inner.len()

    /** Check if the map is empty. */
    fun isEmpty(): Boolean = inner.isEmpty()

    /** Iterate over the entries as (key, value) pairs. */
    fun iter(): Sequence<Pair<K, V>> = inner.iter()

    /** Iterate over the entries with hashed keys. */
    fun iterHashed(): Sequence<Pair<Hashed<K>, V>> = inner.iterHashed()

    /** Iterate over the entries, with mutable values. */
    fun iterMut(): Sequence<Pair<K, V>> = inner.iterMut()

    /** Iterate over the keys. */
    fun keys(): Sequence<K> = inner.keys()

    /** Iterate over the values. */
    fun values(): Sequence<V> = inner.values()

    /** Iterate over the values, with mutable values. */
    fun valuesMut(): Sequence<V> = inner.valuesMut()

    /**
     * Get a reference to the value associated with the given key.
     */
    fun get(key: K): V? = inner.get(key)

    /**
     * Get a reference to the value associated with the given key using [Equivalent].
     */
    fun <Q> get(key: Q): V? where Q : Equivalent<K> = inner.get(key)

    /**
     * Get a mutable reference to the value associated with the given key.
     */
    fun getMut(key: K): V? = inner.get(key)

    /**
     * Get a mutable reference to the value associated with the given key using [Equivalent].
     */
    fun <Q> getMut(key: Q): V? where Q : Equivalent<K> = inner.get(key)

    /** Find an entry by an index. */
    fun getIndex(index: Int): Pair<K, V>? = inner.getIndex(index)

    /**
     * Find an entry index for a given key.
     */
    fun getIndexOf(key: K): Int? = inner.getIndexOf(key)

    /** Find an entry index for a given key using [Equivalent]. */
    fun <Q> getIndexOf(key: Q): Int? where Q : Equivalent<K> = inner.getIndexOf(key)

    /**
     * Check if the map contains the given key.
     */
    fun containsKey(key: K): Boolean = inner.getIndexOf(key) != null

    /** Check if the map contains the given key using [Equivalent]. */
    fun <Q> containsKey(key: Q): Boolean where Q : Equivalent<K> = inner.getIndexOf(key) != null

    /**
     * Insert an entry into the map. Returns the previous value if the key existed.
     */
    fun insert(key: K, value: V): V? = inner.insert(key, value)

    /**
     * Remove an entry by key. Uses shift-remove to preserve iteration order.
     */
    fun remove(key: K): V? = inner.shiftRemove(key)

    /** Remove an entry by key using [Equivalent]. Uses shift-remove to preserve iteration order. */
    fun <Q> remove(key: Q): V? where Q : Equivalent<K> = inner.shiftRemove(key)

    /** Clear the map. */
    fun clear() = inner.clear()

    /** Get an entry by key. */
    fun entry(key: K): Entry<K, V> = inner.entry(key)

    /**
     * Extend the map with entries from an iterable.
     */
    fun extend(iter: Iterable<Pair<K, V>>) {
        for ((k, v) in iter) {
            inner.insert(k, v)
        }
    }

    fun intoIter(): Iterator<Pair<K, V>> = inner.intoIter()

    fun fmt(): String = toString()

    fun eq(other: OrderedMap<K, V>): Boolean = equals(other)

    fun hash(state: StarlarkHasher) {
        inner.hashOrdered(state)
    }

    fun strongHash(state: StarlarkHasher) {
        state.writeU64(len().toULong())
        for ((k, v) in iter()) {
            state.write(k.toString().encodeToByteArray())
            state.write(v.toString().encodeToByteArray())
        }
    }

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> = inner.serialize(serializer)

    override fun iterator(): Iterator<Pair<K, V>> = inner.iterator()

    /**
     * Ordered equality: two [OrderedMap]s are equal iff they contain the same entries
     * in the same iteration order.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrderedMap<*, *>) return false
        if (len() != other.len()) return false
        val thisIter = iter().iterator()
        val otherIter = other.iter().iterator()
        while (thisIter.hasNext()) {
            if (!otherIter.hasNext()) return false
            val (k, v) = thisIter.next()
            val (ok, ov) = otherIter.next()
            if (k != ok || v != ov) return false
        }
        return true
    }

    /**
     * Hash based on ordered iteration of entries.
     *
     * incorporated via the Hashed wrapper's hash). In Kotlin we hash both
     * key and value in iteration order to remain consistent with [equals].
     */
    override fun hashCode(): Int {
        var result = 1
        for ((k, v) in iter()) {
            result = 31 * result + (k?.hashCode() ?: 0)
            result = 31 * result + (v?.hashCode() ?: 0)
        }
        return result
    }

    override fun toString(): String = iter().joinToString(", ", "{", "}") { (k, v) -> "$k=$v" }
}

fun <K, V> orderedMapOf(vararg pairs: Pair<K, V>): OrderedMap<K, V> {
    val map = OrderedMap.withCapacity<K, V>(pairs.size)
    for ((k, v) in pairs) {
        map.insert(k, v)
    }
    return map
}

/**
 * Sort the map by keys.
 */
fun <K : Comparable<K>, V> OrderedMap<K, V>.sortKeys() {
    inner.sortKeys()
}

fun <K : Comparable<K>, V : Comparable<V>> OrderedMap<K, V>.partialCmp(other: OrderedMap<K, V>): Int? = compareTo(other)

fun <K : Comparable<K>, V : Comparable<V>> OrderedMap<K, V>.cmp(other: OrderedMap<K, V>): Int = compareTo(other)

/**
 * Compare two [OrderedMap]s lexicographically by their iteration order.
 */
operator fun <K : Comparable<K>, V : Comparable<V>> OrderedMap<K, V>.compareTo(
    other: OrderedMap<K, V>,
): Int {
    val thisIter = iter().iterator()
    val otherIter = other.iter().iterator()
    while (thisIter.hasNext() && otherIter.hasNext()) {
        val (tk, tv) = thisIter.next()
        val (ok, ov) = otherIter.next()
        val keyCmp = tk.compareTo(ok)
        if (keyCmp != 0) return keyCmp
        val valCmp = tv.compareTo(ov)
        if (valCmp != 0) return valCmp
    }
    return when {
        thisIter.hasNext() -> 1
        otherIter.hasNext() -> -1
        else -> 0
    }
}
