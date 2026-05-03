// port-lint: source small_set.rs
package io.github.kotlinmania.starlarkmap.smallset

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
import io.github.kotlinmania.starlarkmap.smallmap.SmallMap
import io.github.kotlinmania.starlarkmap.smallmap.sortKeys

/**
 * A set with deterministic iteration order.
 *
 * Mirrors upstream `pub struct SmallSet<T>(SmallMap<T, ()>);` — a tuple struct
 * wrapping [SmallMap] with `Unit` as the value type.
 */
class SmallSet<T> internal constructor(
    internal val inner: SmallMap<T, Unit>,
) {
    constructor() : this(SmallMap.new())

    companion object {
        fun <T> new(): SmallSet<T> = SmallSet(SmallMap.new())

        fun <T> withCapacity(n: Int): SmallSet<T> = SmallSet(SmallMap.withCapacity(n))

        fun <T> fromIterator(iter: Iterable<T>): SmallSet<T> {
            val set = SmallSet<T>()
            for (t in iter) {
                set.insert(t)
            }
            return set
        }
    }

    fun isEmpty(): Boolean = inner.isEmpty()

    fun len(): Int = inner.len()

    fun clear() {
        inner.clear()
    }

    fun iter(): Sequence<T> = inner.keys()

    fun iterHashed(): Sequence<Hashed<T>> = inner.iterHashed().map { it.first }

    fun intoIterHashed(): Sequence<Hashed<T>> = iterHashed()

    /** Returns a reference to the first item. */
    fun first(): T? = inner.first()?.first

    /** Returns a reference to the last item. */
    fun last(): T? = inner.last()?.first

    /** Get an element by index. */
    fun getIndex(index: Int): T? = inner.getIndex(index)?.first

    /** Get the index of an element in the set. */
    fun getIndexOf(value: T): Int? = inner.getIndexOf(value)

    /** Get the index of an element using [Equivalent]. */
    fun <Q> getIndexOf(value: Q): Int? where Q : Equivalent<T> = inner.getIndexOf(value)

    /** Return a reference to the value stored in the set, if it is present. */
    fun get(value: T): T? {
        val index = inner.getIndexOf(value) ?: return null
        return inner.entries.keyAt(index)
    }

    /** Return a reference to the value stored in the set using [Equivalent]. */
    fun <Q> get(value: Q): T? where Q : Equivalent<T> {
        val index = inner.getIndexOf(value) ?: return null
        return inner.entries.keyAt(index)
    }

    /** Query the set by a prehashed value. */
    fun <Q> getHashed(value: Hashed<Q>): T? where Q : Equivalent<T> {
        val index = inner.getIndexOfHashed(value) ?: return null
        return inner.entries.keyAt(index)
    }

    /** Check if the set contains an element. */
    fun contains(value: T): Boolean = inner.getIndexOf(value) != null

    /** Check if the set contains an element using [Equivalent]. */
    fun <Q> contains(value: Q): Boolean where Q : Equivalent<T> = inner.getIndexOf(value) != null

    fun addAll(values: Iterable<Hashed<T>>) {
        for (v in values) {
            insertHashed(v)
        }
    }

    fun containsHashedByValue(key: Hashed<T>): Boolean {
        return inner.getIndexOfHashedByValue(key) != null
    }

    fun <Q> containsHashed(key: Hashed<Q>): Boolean where Q : Equivalent<T> {
        return inner.getIndexOfHashed(key) != null
    }

    /** Insert the element into the set. Return `true` iff the element was inserted. */
    fun insert(value: T): Boolean {
        return insertHashed(Hashed.new(value))
    }

    /** Insert the element into the set without checking for a duplicate entry. */
    fun insertUniqueUnchecked(value: T) {
        insertHashedUniqueUnchecked(Hashed.new(value))
    }

    fun insertHashed(value: Hashed<T>): Boolean {
        if (inner.getIndexOfHashedByValue(value) != null) return false
        inner.insertHashedUniqueUnchecked(value, Unit)
        return true
    }

    fun insertHashedUniqueUnchecked(value: Hashed<T>) {
        inner.insertHashedUniqueUnchecked(value, Unit)
    }

    /** Remove and return the last element, or null if empty. */
    fun pop(): T? = inner.pop()?.first

    /** Remove the element from the set if it is present, and return the removed element. */
    fun take(value: T): T? = inner.shiftRemoveEntry(value)?.first

    /** Remove the element using [Equivalent], and return the removed element. */
    fun <Q> take(value: Q): T? where Q : Equivalent<T> = inner.shiftRemoveEntry(value)?.first

    fun shiftRemoveHashedByValue(value: Hashed<T>): Boolean {
        return inner.shiftRemoveHashedByValue(value) != null
    }

    fun <Q> shiftRemoveHashed(value: Hashed<Q>): Boolean where Q : Equivalent<T> {
        return inner.shiftRemoveHashed(value) != null
    }

    /**
     * Remove the element from the set if it is present.
     *
     * Time complexity of this operation is *O(N)* where *N* is the number of entries in the set.
     */
    fun shiftRemove(key: T): Boolean = inner.shiftRemove(key) != null

    /**
     * Remove the element by index. This is *O(N)* operation.
     */
    fun shiftRemoveIndexHashed(i: Int): Hashed<T>? = inner.shiftRemoveIndexHashed(i)?.first

    /**
     * Remove the element by index. This is *O(N)* operation.
     */
    fun shiftRemoveIndex(i: Int): T? = inner.shiftRemoveIndex(i)?.first

    /**
     * Insert entry if it doesn't exist.
     *
     * Return the resulting entry in the set.
     */
    fun getOrInsert(value: T): T {
        val existing = get(value)
        if (existing != null) return existing
        inner.insertHashedUniqueUnchecked(Hashed.new(value), Unit)
        return value
    }

    /**
     * Insert entry if it doesn't exist.
     *
     * Return the resulting entry in the set.
     */
    fun <Q> getOrInsertOwned(value: Q, toOwned: (Q) -> T): T where Q : Equivalent<T> {
        val existing = get(value)
        if (existing != null) return existing
        val owned = toOwned(value)
        inner.insertHashedUniqueUnchecked(Hashed.new(owned), Unit)
        return owned
    }

    /** Reserve capacity for at least [additional] more elements. */
    fun reserve(additional: Int) {
        inner.reserve(additional)
    }

    /** Current capacity of the set. */
    fun capacity(): Int = inner.capacity()

    /** Reverse the iteration order of the set. */
    fun reverse() {
        inner.reverse()
    }

    /** Iterator over elements of this set which are not in the other set. */
    fun difference(other: SmallSet<T>): Difference<T> = Difference(iter().iterator(), this, other)

    /**
     * Iterator over union of two sets.
     *
     * Iteration order is: elements of this set followed by elements in the
     * other set not present in this set.
     */
    fun union(other: SmallSet<T>): Union<T> = Union(iter().iterator(), other.difference(this))

    /** Equal if entries are equal in iteration order. */
    fun eqOrdered(other: SmallSet<T>): Boolean = inner.eqOrdered(other.inner)

    /** Extend with elements from an iterable. */
    fun extend(iter: Iterable<T>) {
        for (t in iter) {
            insert(t)
        }
    }

    operator fun iterator(): Iterator<T> = iter().iterator()

    /**
     * Two sets are equal if they contain the same elements regardless of iteration order,
     * mirroring the upstream `PartialEq` impl on [SmallMap].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SmallSet<*>) return false
        if (inner.len() != other.inner.len()) return false
        // Mirrors upstream small_set.rs:63-72 — set equality by membership of
        // each element from this in `other`. Compare via [Hashed.equals],
        // which checks hash and key equality without needing a T binding.
        val otherHashed = other.iterHashed().toSet()
        for (h in iterHashed()) {
            if (h !in otherHashed) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var sum = 0
        for (e in iterHashed()) {
            sum += e.hashCode()
        }
        return sum
    }

    override fun toString(): String =
        iter().joinToString(prefix = "{", postfix = "}", separator = ", ") { it.toString() }
}

/** Iterator over the difference of two sets. */
class Difference<T> internal constructor(
    private val iter: Iterator<T>,
    private val source: SmallSet<T>,
    private val other: SmallSet<T>,
) : Iterator<T> {
    private var nextItem: T? = null
    private var hasNextItem: Boolean = false
    private var advanced: Int = 0

    private fun advance() {
        while (iter.hasNext()) {
            val item = iter.next()
            advanced += 1
            if (!other.contains(item)) {
                nextItem = item
                hasNextItem = true
                return
            }
        }
    }

    override fun hasNext(): Boolean {
        if (!hasNextItem) advance()
        return hasNextItem
    }

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        val v = nextItem!!
        nextItem = null
        hasNextItem = false
        return v
    }

    /** Lower and upper bound on remaining items. */
    fun sizeHint(): Pair<Int, Int?> {
        val remainingInner = source.len() - advanced + (if (hasNextItem) 1 else 0)
        return Pair(
            (remainingInner - other.len()).coerceAtLeast(0),
            remainingInner,
        )
    }
}

/** Iterator over a union of two sets. */
class Union<T> internal constructor(
    private val first: Iterator<T>,
    private val second: Difference<T>,
) : Iterator<T> {
    override fun hasNext(): Boolean = first.hasNext() || second.hasNext()

    override fun next(): T = if (first.hasNext()) first.next() else second.next()
}

/** Sort entries. */
fun <T : Comparable<T>> SmallSet<T>.sort() {
    inner.sortKeys()
}
