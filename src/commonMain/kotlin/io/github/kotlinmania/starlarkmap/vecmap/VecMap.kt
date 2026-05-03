// port-lint: source vec_map.rs
package io.github.kotlinmania.starlarkmap.vecmap

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
import io.github.kotlinmania.starlarkmap.vec2.Vec2

internal class VecMap<K, V> private constructor(
    internal val buckets: Vec2<Pair<K, V>, StarlarkHashValue>,
) {
    companion object {
        fun <K, V> new(): VecMap<K, V> = VecMap(Vec2.new())

        fun <K, V> default(): VecMap<K, V> = new()

        fun <K, V> withCapacity(n: Int): VecMap<K, V> = VecMap(Vec2.withCapacity(n))
    }

    fun reserve(additional: Int) {
        buckets.reserve(additional)
    }

    fun capacity(): Int = buckets.capacity()

    fun len(): Int = buckets.len()

    fun isEmpty(): Boolean = buckets.isEmpty()

    fun clear() {
        buckets.clear()
    }

    fun getIndexOfHashedRaw(hash: StarlarkHashValue, eq: (K) -> Boolean): Int? {
        val hashes = buckets.bbb()
        val pairs = buckets.aaa()
        for (i in hashes.indices) {
            if (hashes[i] == hash && eq(pairs[i].first)) return i
        }
        return null
    }

    fun <Q> getIndexOfHashed(key: Hashed<Q>): Int? where Q : Equivalent<K> {
        val q = key.key()
        return getIndexOfHashedRaw(key.hash()) { k -> q.equivalent(k) }
    }

    fun getIndex(index: Int): Pair<K, V>? {
        val pair = buckets.aaa().getOrNull(index) ?: return null
        return pair
    }

    fun getUnchecked(index: Int): Pair<Hashed<K>, V> {
        require(index in 0 until buckets.len())
        val (k, v) = buckets.aaa()[index]
        val h = buckets.bbb()[index]
        return Pair(Hashed.newUnchecked(h, k), v)
    }

    fun getUncheckedMut(index: Int): Pair<Hashed<K>, V> = getUnchecked(index)

    fun insertHashedUniqueUnchecked(key: Hashed<K>, value: V) {
        buckets.push(Pair(key.intoKey(), value), key.hash())
    }

    fun <Q> removeHashedEntry(key: Hashed<Q>): Pair<K, V>? where Q : Equivalent<K> {
        val index = getIndexOfHashed(key) ?: return null
        val (pair, _) = buckets.remove(index)
        return pair
    }

    fun remove(index: Int): Pair<Hashed<K>, V> {
        val (pair, hash) = buckets.remove(index)
        return Pair(Hashed.newUnchecked(hash, pair.first), pair.second)
    }

    fun pop(): Pair<Hashed<K>, V>? {
        val (pair, hash) = buckets.pop() ?: return null
        return Pair(Hashed.newUnchecked(hash, pair.first), pair.second)
    }

    fun values(): Sequence<V> = buckets.aaa().asSequence().map { it.second }

    fun valuesMut(): Sequence<V> = values()

    fun keys(): Sequence<K> = buckets.aaa().asSequence().map { it.first }

    fun intoIter(): Iterator<Pair<K, V>> = iter().iterator()

    fun iter(): Sequence<Pair<K, V>> = buckets.aaa().asSequence()

    fun iterHashed(): Sequence<Pair<Hashed<K>, V>> {
        val pairs = buckets.aaa()
        val hashes = buckets.bbb()
        return pairs.indices.asSequence().map { i ->
            Pair(Hashed.newUnchecked(hashes[i], pairs[i].first), pairs[i].second)
        }
    }

    fun intoIterHashed(): Iterator<Pair<Hashed<K>, V>> = iterHashed().iterator()

    fun iterMut(): Sequence<Pair<K, V>> = iter()

    fun iterMutUnchecked(): Sequence<Pair<K, V>> = iter()

    /** Equal if entries are equal in the iterator order. */
    fun eqOrdered(other: VecMap<K, V>): Boolean = buckets == other.buckets

    /** Hash entries in the iterator order. */
    fun hashOrdered(): Int = buckets.hashCode()

    fun reverse() {
        buckets.aaaMut().reverse()
        buckets.bbbMut().reverse()
    }

    fun retain(f: (K, V) -> Boolean) {
        buckets.retain { pair, _ -> f(pair.first, pair.second) }
    }
}

internal fun <K : Comparable<K>, V> VecMap<K, V>.sortKeys() {
    buckets.sortBy { (a, _), (b, _) -> a.first.compareTo(b.first) }
}

internal fun <K : Comparable<K>, V> VecMap<K, V>.isSortedByKey(): Boolean {
    val pairs = buckets.aaa()
    for (i in 1 until pairs.size) {
        if (pairs[i - 1].first > pairs[i].first) return false
    }
    return true
}
