// port-lint: source tests:src/unordered_map.rs
package io.github.kotlinmania.starlarkmap.unorderedmap

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
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class UnorderedMapTest {
    @Test
    fun testHash() {
        val a = UnorderedMap.fromIterator(listOf(Pair(1, 2), Pair(3, 4)))
        val b = UnorderedMap.fromIterator(listOf(Pair(3, 4), Pair(1, 2)))

        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun testEq() {
        val a = UnorderedMap.fromIterator(listOf(Pair(1, 2), Pair(3, 4)))
        val b = UnorderedMap.fromIterator(listOf(Pair(3, 4), Pair(1, 2)))
        val c = UnorderedMap.fromIterator(listOf(Pair(1, 2), Pair(9, 10)))
        val d = UnorderedMap.fromIterator(listOf(Pair(1, 2), Pair(3, 4), Pair(5, 6)))
        assertEquals(a, b)
        assertNotEquals(a, c)
        assertNotEquals(a, d)
    }

    @Test
    fun testInsertRemove() {
        val map = UnorderedMap.new<Int, Int>()
        assertNull(map.insert(1, 2))
        assertEquals(UnorderedMap.fromIterator(listOf(Pair(1, 2))), map)
        assertEquals(2, map.insert(1, 3))
        assertEquals(UnorderedMap.fromIterator(listOf(Pair(1, 3))), map)
        assertEquals(3, map.remove(1))
        assertEquals(UnorderedMap.new<Int, Int>(), map)
    }

    @Test
    fun testEntriesSorted() {
        val map = UnorderedMap.new<Int, Int>()
        map.insert(1, 2)
        map.insert(5, 6)
        map.insert(3, 4)
        assertEquals(
            listOf(Pair(1, 2), Pair(3, 4), Pair(5, 6)),
            map.entriesSorted(),
        )
    }

    @Test
    fun testRetain() {
        val map = UnorderedMap.new<String, StringBuilder>()
        for (i in 0 until 1000) {
            map.insert("key$i", StringBuilder("value$i"))
        }

        map.retain { k, v ->
            v.append('x')
            k.endsWith('0')
        }

        assertEquals(100, map.len())
        for (i in 0 until 1000) {
            if (i % 10 == 0) {
                assertEquals("value${i}x", map.get("key$i")!!.toString())
            } else {
                assertFalse(map.containsKey("key$i"))
            }
        }
    }
}
