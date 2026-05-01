// port-lint: source tests:src/ordered_set.rs
package io.github.kotlinmania.starlarkmap.orderedset

/*
 * Copyright 2019 The Starlark in Rust Authors.
 * Copyright (c) Facebook, Inc. and its affiliates.
 * Copyright (c) 2025 Sydney Renee, The Solace Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrderedSetTest {
    @Test
    fun testKeysAreNotHashedWhenMapIsHashed() {
        class Tester {
            /** Number of times [hashCode] was called. */
            var hashCount: Int = 0

            override fun equals(other: Any?): Boolean = true

            override fun hashCode(): Int {
                hashCount += 1
                return 0
            }
        }

        val set = OrderedSet.fromIterator(listOf(Tester()))
        assertEquals(1, set.iter().first().hashCount)

        set.hashCode()
        assertEquals(1, set.iter().first().hashCount)

        set.hashCode()
        assertEquals(1, set.iter().first().hashCount)

        set.hashCode()
        assertEquals(1, set.iter().first().hashCount)
    }

    @Test
    fun testInsertUnique() {
        val set: OrderedSet<IntBox> = OrderedSet.new()
        val inserted = set.tryInsert(IntBox(1))
        assertNull(inserted)

        val one = IntBox(1)
        val err = set.tryInsert(one)
        assertNotNull(err)
        assertTrue(err.value === one)
    }

    private class IntBox(val v: Int) {
        override fun equals(other: Any?): Boolean = other is IntBox && other.v == v
        override fun hashCode(): Int = v.hashCode()
    }
}
