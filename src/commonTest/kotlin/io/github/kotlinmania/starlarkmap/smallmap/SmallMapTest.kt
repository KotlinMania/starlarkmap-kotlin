// port-lint: source tests:small_map.rs
package io.github.kotlinmania.starlarkmap.smallmap

/*
 * Copyright 2019 The Starlark in Rust Authors.
 * Copyright (c) Facebook, Inc. and its affiliates.
 * Copyright (c) 2026 Sydney Renee, The Solace Project
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SmallMapTest {
    @Test
    fun testPopSmall() {
        val map = SmallMap.new<Int, Int>()
        for (i in 0..5) {
            map.insert(i, i * 10)
        }
        for (i in 5 downTo 0) {
            assertEquals(Pair(i, i * 10), map.pop())
            map.stateCheck()
        }
        assertTrue(map.isEmpty())
    }

    @Test
    fun testPopLarge() {
        val map = SmallMap.new<Int, Int>()
        for (i in 0..500) {
            map.insert(i, i * 10)
        }
        for (i in 500 downTo 0) {
            assertEquals(Pair(i, i * 10), map.pop())
            if (i % 100 == 0) {
                map.stateCheck()
            }
        }
        assertTrue(map.isEmpty())
    }

    @Test
    fun testFirst() {
        val map = SmallMap.new<Int, Int>()
        map.insert(1, 10)
        assertEquals(Pair(1, 10), map.first())
        map.insert(2, 20)
        assertEquals(Pair(1, 10), map.first())
        map.shiftRemove(1)
        assertEquals(Pair(2, 20), map.first())
    }

    @Test
    fun testLast() {
        val map = SmallMap.new<Int, Int>()
        map.insert(1, 10)
        assertEquals(Pair(1, 10), map.last())
        map.insert(2, 20)
        assertEquals(Pair(2, 20), map.last())
        map.insert(1, 100)
        assertEquals(Pair(2, 20), map.last())
    }

    @Test
    fun testSortKeysNoIndex() {
        val map = SmallMap.new<Int, Int>()
        map.insert(2, 20)
        map.insert(1, 10)
        map.insert(3, 30)
        map.sortKeys()
        assertEquals(listOf(Pair(1, 10), Pair(2, 20), Pair(3, 30)), map.iter().toList())
        assertEquals(10, map.get(1))
        assertEquals(20, map.get(2))
        assertEquals(30, map.get(3))
    }

    @Test
    fun testSortKeysWithIndex() {
        val map = SmallMap.new<Int, Int>()
        for (i in 1..100) {
            map.insert(i, i * 10)
        }
        map.sortKeys()
        assertEquals((1..100).map { Pair(it, it * 10) }, map.iter().toList())
        for (i in 1..100) {
            assertEquals(i * 10, map.get(i))
        }
    }

    @Test
    fun testEqOrdered() {
        val m0 = SmallMap.fromIter(listOf(Pair(1, 2), Pair(3, 4)))
        val m1 = SmallMap.fromIter(listOf(Pair(1, 2), Pair(3, 4)))
        val m2 = SmallMap.fromIter(listOf(Pair(3, 4), Pair(1, 2)))
        val m3 = SmallMap.fromIter(listOf(Pair(3, 4)))
        assertTrue(m0.eqOrdered(m0))
        assertTrue(m0.eqOrdered(m1))
        assertFalse(m0.eqOrdered(m2))
        assertFalse(m0.eqOrdered(m3))
    }

    @Test
    fun testShiftRemove() {
        val map = SmallMap.fromIter((0 until 100).map { Pair(it, it * 10) })
        assertEquals(Pair(1, 10), map.shiftRemoveEntry(1))
        assertEquals(30, map.get(3))
        map.stateCheck()
    }

    @Test
    fun testShiftRemoveLast() {
        val map = SmallMap.fromIter((0 until 100).map { Pair(it, it * 10) })
        assertEquals(Pair(99, 990), map.shiftRemoveEntry(99))
        assertEquals(980, map.get(98))
        map.stateCheck()
    }

    @Test
    fun testShiftRemoveIndex() {
        val map = SmallMap.fromIter((0 until 100).map { Pair(it, it * 10) })
        map.shiftRemoveIndex(5)
        assertEquals(40, map.get(4))
        assertNull(map.get(5))
        assertEquals(60, map.get(6))
        map.stateCheck()
    }

    @Test
    fun testReverseSmall() {
        val map = SmallMap.new<String, String>()
        map.insert("a", "b")
        map.insert("c", "d")
        map.reverse()
        assertEquals("b", map.get("a"))
        assertEquals("d", map.get("c"))
        assertEquals(listOf(Pair("c", "d"), Pair("a", "b")), map.intoIter().asSequence().toList())
    }

    @Test
    fun testReverseLarge() {
        val map = SmallMap.new<String, String>()
        for (i in 0 until 100) {
            map.insert(i.toString(), (i * 10).toString())
        }
        val expected = map.iter().toList().asReversed()
        map.reverse()
        for (i in 0 until 100) {
            assertEquals((i * 10).toString(), map.get(i.toString()))
        }
        assertEquals(expected, map.iter().toList())
    }
}
