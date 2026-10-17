// port-lint: source sorted_vec.rs
package io.github.kotlinmania.starlarkmap.sortedvec

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

import io.github.kotlinmania.allocative.Allocative
import io.github.kotlinmania.allocative.Visitor
import io.github.kotlinmania.allocative.key.Key

/**
 * Type which enforces that its elements are sorted. That's it.
 *
 */
class SortedVec<T> private constructor(
    private val vec: MutableList<T>,
) : Iterable<T>,
    Allocative {
    interface Item

    interface IntoIter

    companion object {
        /** Construct an empty [SortedVec]. */
        fun <T> new(): SortedVec<T> = SortedVec(mutableListOf())

        /**
         * Construct without checking that the elements are sorted.
         *
         * In debug builds, asserts that the list is sorted.
         */
        fun <T : Comparable<*>> newUnchecked(vec: List<T>): SortedVec<T> {
            check(vec.zipWithNext().all { (a, b) -> compareValues(a, b) <= 0 }) {
                "SortedVec::new_unchecked called with unsorted elements"
            }
            return SortedVec(vec.toMutableList())
        }

        fun <T> default(): SortedVec<T> = new()

        /**
         * Create a [SortedVec] from a list, sorting it first.
         */
        fun <T : Comparable<*>> from(vec: List<T>): SortedVec<T> {
            val sorted = vec.toMutableList()
            sorted.sortWith(compareBy { it })
            return SortedVec(sorted)
        }

        /**
         * Create a [SortedVec] from an iterable, sorting the collected elements.
         */
        fun <T : Comparable<*>> fromIter(iter: Iterable<T>): SortedVec<T> {
            val vec = iter.toMutableList()
            vec.sortWith(compareBy { it })
            return SortedVec(vec)
        }
    }

    /** Iterate over the elements. */
    fun iter(): Sequence<T> = vec.asSequence()

    fun intoIter(): Iterator<T> = vec.iterator()

    override fun visit(visitor: Visitor) {
        val v = visitor.enterSelfSized(SortedVec::class, 24)
        v.visitFieldWith(Key.new("vec"), 24) { fieldVisitor ->
            for (elem in vec) {
                if (elem is Allocative) elem.visit(fieldVisitor)
            }
        }
        v.exit()
    }

    override fun iterator(): Iterator<T> = vec.iterator()

    /**
     * Ordered equality: two [SortedVec]s are equal iff they contain the same elements
     * in the same order.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SortedVec<*>) return false
        return vec == other.vec
    }

    /**
     * Hash based on the sorted elements.
     */
    override fun hashCode(): Int = vec.hashCode()

    override fun toString(): String = vec.toString()
}

/**
 * Lexicographic comparison, matching Rust's derived `Ord` implementation for
 * `SortedVec<T>` where `T: Ord`.
 */
operator fun <T : Comparable<*>> SortedVec<T>.compareTo(other: SortedVec<T>): Int {
    val thisIter = iter().iterator()
    val otherIter = other.iter().iterator()
    while (thisIter.hasNext() && otherIter.hasNext()) {
        val cmp = compareValues(thisIter.next(), otherIter.next())
        if (cmp != 0) return cmp
    }
    return when {
        thisIter.hasNext() -> 1
        otherIter.hasNext() -> -1
        else -> 0
    }
}
