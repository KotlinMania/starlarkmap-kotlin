// port-lint: source sorted_map.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.starlarkmap.sortedmap

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
import io.github.kotlinmania.starlarkmap.orderedmap.OrderedMap
import io.github.kotlinmania.starlarkmap.orderedmap.sortKeys
import io.github.kotlinmania.starlarkmap.smallmap.SmallMap
import kotlin.native.HiddenFromObjC

/**
 * [OrderedMap] but with keys sorted.
 *
 */
// generic by design: key-sorted map container; key/value params are the public contract.
@HiddenFromObjC
class SortedMap<K, V> internal constructor(
    private val map: OrderedMap<K, V>,
) : Iterable<Pair<K, V>>,
    Serialize {
    interface Item

    interface IntoIter

    companion object {
        /** Construct an empty [SortedMap]. */
        fun <K, V> new(): SortedMap<K, V> where K : Comparable<K> =
            SortedMap(OrderedMap.new())

        fun <K, V> default(): SortedMap<K, V> where K : Comparable<K> = new()

        /**
         * Create a [SortedMap] from an iterable of key-value pairs.
         */
        fun <K, V> fromIterator(iter: Iterable<Pair<K, V>>): SortedMap<K, V> where K : Comparable<K> {
            val map = OrderedMap.fromIterator(iter)
            return from(map)
        }

        fun <K, V> fromIter(iter: Iterable<Pair<K, V>>): SortedMap<K, V> where K : Comparable<K> =
            fromIterator(iter)

        fun <K, V> from(map: OrderedMap<K, V>): SortedMap<K, V> where K : Comparable<K> {
            map.sortKeys()
            return SortedMap(map)
        }

        fun <K, V> from(map: SmallMap<K, V>): SortedMap<K, V> where K : Comparable<K> =
            from(OrderedMap.from(map))

        fun <K, V, D : Deserializer> deserialize(
            deserializer: D,
            keyDeserialize: Deserialize<K>,
            valueDeserialize: Deserialize<V>,
        ): SerdeResult<SortedMap<K, V>> where K : Comparable<K> =
            OrderedMap.deserialize(deserializer, keyDeserialize, valueDeserialize).map { SortedMap(it) }

        fun <K, V> deserialize(
            keyDeserialize: Deserialize<K>,
            valueDeserialize: Deserialize<V>,
        ): Deserialize<SortedMap<K, V>> where K : Comparable<K> =
            object : Deserialize<SortedMap<K, V>> {
                override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<SortedMap<K, V>> =
                    SortedMap.deserialize(deserializer, keyDeserialize, valueDeserialize)
            }
    }

    /** Iterate over the entries. */
    fun iter(): Sequence<Pair<K, V>> = map.iter()

    /** Iterate over the entries, with mutable values. */
    fun iterMut(): Sequence<Pair<K, V>> = map.iterMut()

    /** Iterate over the keys. */
    fun keys(): Sequence<K> = map.keys()

    /** Iterate over the values. */
    fun values(): Sequence<V> = map.values()

    /** Iterate over the values mutably. */
    fun valuesMut(): Sequence<V> = map.valuesMut()

    /** Return the number of elements in the map. */
    fun len(): Int = map.len()

    /** Check if the map is empty. */
    fun isEmpty(): Boolean = map.isEmpty()

    /**
     * Get a reference to the value associated with the given key.
     */
    fun get(key: K): V? = map.get(key)

    /** Get a reference to the value using [Equivalent]. */
    fun <Q> get(key: Q): V? where Q : Equivalent<K> = map.get(key)

    /**
     * Get a mutable reference to the value associated with the given key.
     */
    fun getMut(key: K): V? = map.getMut(key)

    /** Get a mutable reference to the value using [Equivalent]. */
    fun <Q> getMut(key: Q): V? where Q : Equivalent<K> = map.getMut(key)

    /**
     * Check if the map contains the given key.
     */
    fun containsKey(key: K): Boolean = map.containsKey(key)

    /** Check if the map contains the given key using [Equivalent]. */
    fun <Q> containsKey(key: Q): Boolean where Q : Equivalent<K> = map.containsKey(key)

    /** Iterate over the map with hashes. */
    fun iterHashed() = map.iterHashed()

    fun intoIter(): Iterator<Pair<K, V>> = map.intoIter()

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> = map.serialize(serializer)

    override fun iterator(): Iterator<Pair<K, V>> = map.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SortedMap<*, *>) return false
        return map == other.map
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}

fun <K : Comparable<K>, V> sortedMapOf(vararg pairs: Pair<K, V>): SortedMap<K, V> =
    SortedMap.fromIter(pairs.asIterable())

operator fun <K : Comparable<K>, V : Comparable<V>> SortedMap<K, V>.compareTo(other: SortedMap<K, V>): Int {
    val thisIter = iter().iterator()
    val otherIter = other.iter().iterator()
    while (thisIter.hasNext() && otherIter.hasNext()) {
        val (k1, v1) = thisIter.next()
        val (k2, v2) = otherIter.next()
        val kCmp = k1.compareTo(k2)
        if (kCmp != 0) return kCmp
        val vCmp = v1.compareTo(v2)
        if (vCmp != 0) return vCmp
    }
    return when {
        thisIter.hasNext() -> 1
        otherIter.hasNext() -> -1
        else -> 0
    }
}
