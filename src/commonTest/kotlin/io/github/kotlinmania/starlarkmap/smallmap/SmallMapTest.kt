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
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SmallMapTest {
    @Test
    fun emptyMap() {
        val m = SmallMap.new<Byte, String>()
        assertTrue(m.isEmpty())
        assertEquals(0, m.len())
        assertNull(m.iter().firstOrNull())
    }

    @Test
    fun fewEntries() {
        val entries1 = listOf(Pair(0, 'a'), Pair(1, 'b'))
        val m1 = SmallMap.fromIter(entries1)

        val entries2 = listOf(Pair(1, 'b'), Pair(0, 'a'))
        val m2 = SmallMap.fromIter(entries2)
        assertFalse(m1.isEmpty())
        assertEquals(2, m1.len())
        assertFalse(m2.isEmpty())
        assertEquals(2, m2.len())

        assertEquals(entries1, m1.iter().toList())
        assertEquals(entries2, m2.iter().toList())
        assertNotEquals(m1.iter().toList(), m2.iter().toList())
        assertEquals(m1.iter().toList(), m1.iter().toList())
        assertEquals(m2.iter().toList(), m2.iter().toList())

        assertEquals('a', m1.get(0))
        assertNull(m1.get(3))
        assertEquals('b', m2.get(1))
        assertNull(m2.get(3))

        assertEquals(Pair(0, 'a'), m1.getIndex(0))
        assertEquals(Pair(1, 'b'), m1.getIndex(1))
        assertNull(m1.getIndex(2))

        val different = SmallMap.fromIter(listOf(Pair(0, 'a'), Pair(1, 'c')))
        assertNotEquals(m1.iter().toList(), different.iter().toList())

        val values1 = m1.iter().toList()
        val values2 = m1.iter().toList()
        assertEquals(values1, values2)
    }

    @Test
    fun manyEntries() {
        val numbers = (0..25).toList()
        val letters = ('a'..'z').toList()

        val entries1 = numbers.zip(letters)
        val m1 = SmallMap.fromIter(entries1)

        val numbersRev = (0..25).reversed().toList()
        val lettersRev = ('a'..'z').reversed().toList()
        val entries2 = numbersRev.zip(lettersRev)
        val m2 = SmallMap.fromIter(entries2)
        assertFalse(m1.isEmpty())
        assertEquals(26, m1.len())
        assertFalse(m2.isEmpty())
        assertEquals(26, m2.len())

        assertEquals(entries1, m1.intoIter().asSequence().toList())
        assertEquals(entries2, m2.intoIter().asSequence().toList())
        assertNotEquals(m1.iter().toList(), m2.iter().toList())
        assertEquals(m1.iter().toList(), m1.iter().toList())
        assertEquals(m2.iter().toList(), m2.iter().toList())

        assertEquals('b', m1.get(1))
        assertNull(m1.get(30))
        assertEquals('a', m2.get(0))
        assertNull(m2.get(30))

        val notM1 = SmallMap.fromIter(entries1).also { it.shiftRemove(1) }
        assertNotEquals(m1.iter().toList(), notM1.iter().toList())

        val values1 = m1.iter().toList()
        val values2 = m1.iter().toList()
        assertEquals(values1, values2)
    }

    @Test
    fun testSmallmapMacro() {
        val map = SmallMap.fromIter(listOf(Pair(1, "a"), Pair(3, "b")))
        val i = map.intoIter()
        assertEquals(Pair(1, "a"), i.next())
        assertEquals(Pair(3, "b"), i.next())
        assertFalse(i.hasNext())
    }

    @Test
    fun testClone() {
        val map = SmallMap.fromIter(listOf(Pair(1, "a"), Pair(3, "b")))
        val values1 = map.iter().toList()
        val values2 = map.iter().toList()
        assertEquals(listOf(Pair(1, "a"), Pair(3, "b")), values1)
        assertEquals(values1, values2)

        val keys1 = map.keys().toList()
        val keys2 = map.keys().toList()
        assertEquals(listOf(1, 3), keys1)
        assertEquals(keys1, keys2)

        val vs1 = map.values().toList()
        val vs2 = map.values().toList()
        assertEquals(listOf("a", "b"), vs1)
        assertEquals(vs1, vs2)
    }

    @Test
    fun testDuplicateHashes() {
        // A type which always gives hash collisions
        data class K(val n: Int) {
            override fun hashCode(): Int = 0
        }

        val map = SmallMap.fromIter(listOf(Pair(K(1), "test"), Pair(K(3), "more")))
        assertEquals("test", map.get(K(1)))
        assertNull(map.get(K(2)))
        assertEquals("more", map.get(K(3)))

        assertNull(map.insert(K(2), "magic"))
        assertEquals("magic", map.get(K(2)))

        assertEquals("test", map.shiftRemove(K(1)))
        assertNull(map.get(K(1)))
        assertEquals(listOf(K(3), K(2)), map.keys().toList())
    }

    @Test
    fun testSmallmapDebug() {
        val s = SmallMap.fromIter(listOf(Pair(1, "test"), Pair(2, "more"))).toString()
        assertEquals("{1: \"test\", 2: \"more\"}", s)
    }

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
    fun testSortKeysUpdatesIndexOnPanic() {
        data class Key(val n: UInt) : Comparable<Key> {
            override fun compareTo(other: Key): Int {
                if (n < 10u && other.n < 10u) {
                    error("panic in compareTo")
                }
                return n.compareTo(other.n)
            }
        }

        val map = SmallMap.new<Key, UInt>()
        for (i in (1..100).reversed()) {
            map.insert(Key(i.toUInt()), i.toUInt() * 10u)
        }
        assertFails {
            map.sortKeys()
        }
        // If the index were not updated on panic, the following call would fail.
        map.stateCheck()
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
