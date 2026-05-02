// port-lint: source tests:src/small_set.rs
package io.github.kotlinmania.starlarkmap.smallset

/*
 * Copyright 2019 The Starlark in Rust Authors.
 * Copyright (c) Facebook, Inc. and its affiliates.
 * Copyright (c) 2026 Sydney Renee, The Solace Project
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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import io.github.kotlinmania.starlarkmap.Equivalent

class SmallSetTest {

    @Test
    fun emptySet() {
        val m = SmallSet<Byte>()
        assertTrue(m.isEmpty())
        assertEquals(0, m.len())
        assertNull(m.iter().firstOrNull())
    }

    @Test
    fun fewEntries() {
        val entries1 = listOf(0, 1)
        val m1 = SmallSet.fromIterator(entries1)

        val entries2 = listOf(1, 0)
        val m2 = SmallSet.fromIterator(entries2)
        assertFalse(m1.isEmpty())
        assertEquals(2, m1.len())
        assertFalse(m2.isEmpty())
        assertEquals(2, m2.len())

        assertEquals(entries1, m1.iter().toList())
        assertEquals(entries2, m2.iter().toList())
        assertNotEquals(m1.iter().toList(), m2.iter().toList())
        assertEquals(m1, m1)
        assertEquals(m2, m2)
        assertEquals(m1, m2)

        assertNotEquals(m1, SmallSet.fromIterator(listOf(1)))
    }

    @Test
    fun manyEntries() {
        val entries1 = ('a'..'z').toList()
        val m1 = SmallSet.fromIterator(entries1)

        assertEquals('b', m1.get('b'))
        assertEquals(1, m1.getIndexOf('b'))

        assertNull(m1.get('!'))
        assertNull(m1.getIndexOf('!'))

        val entries2 = ('a'..'z').toList().reversed()
        val m2 = SmallSet.fromIterator(entries2)
        assertFalse(m1.isEmpty())
        assertEquals(26, m1.len())
        assertFalse(m2.isEmpty())
        assertEquals(26, m2.len())

        assertEquals(entries1, m1.iter().toList())
        assertEquals(entries2, m2.iter().toList())
        assertNotEquals(m1.iter().toList(), m2.iter().toList())
        assertEquals(m1, m1)
        assertEquals(m2, m2)
        assertEquals(m1, m2)

        val notM1 = SmallSet.fromIterator(entries1).also { it.shiftRemove('a') }
        assertNotEquals(m1, notM1)
    }

    @Test
    fun smallSetInserts() {
        val s = SmallSet<Int>()
        assertTrue(s.insert(2))
        assertTrue(s.insert(5))

        assertFalse(s.insert(5))
    }

    @Test
    fun getOrInsert() {
        val set = SmallSet<Boxed>()
        val x = set.getOrInsert(Boxed(1))
        val x1 = set.getOrInsert(Boxed(1))
        assertSame(x, x1)
    }

    @Test
    fun getOrInsertOwned() {
        val set = SmallSet<Boxed>()
        val x = set.getOrInsertOwned(BoxedKey(1)) { Boxed(it.value) }
        val x1 = set.getOrInsertOwned(BoxedKey(1)) { Boxed(it.value) }
        assertSame(x, x1)
    }

    @Test
    fun testFirst() {
        val s = SmallSet<Int>()
        s.insert(1)
        assertEquals(1, s.first())
        s.insert(2)
        assertEquals(1, s.first())
        s.shiftRemove(1)
        assertEquals(2, s.first())
    }

    @Test
    fun testLast() {
        val s = SmallSet<Int>()
        s.insert(1)
        assertEquals(1, s.last())
        s.insert(2)
        assertEquals(2, s.last())
    }

    @Test
    fun testShiftRemove() {
        val h = HashSet<UInt>().apply { add(17u) }
        val s = SmallSet.fromIterator(listOf(17u))
        assertTrue(h.remove(17u))
        assertTrue(s.shiftRemove(17u))
        assertFalse(h.remove(17u))
        assertFalse(s.shiftRemove(17u))
    }

    @Test
    fun testDifference() {
        val a = SmallSet.fromIterator(listOf(1, 2, 3))
        val b = SmallSet.fromIterator(listOf(2, 4, 1))
        val d = a.difference(b).toList()
        assertEquals(listOf(3), d)
    }

    @Test
    fun testUnion() {
        val a = SmallSet.fromIterator(listOf(1, 2, 3))
        val b = SmallSet.fromIterator(listOf(2, 4, 1))
        val d = a.union(b).toList()
        assertEquals(listOf(1, 2, 3, 4), d)
    }

    @Test
    fun testSort() {
        val a = SmallSet.fromIterator(listOf(1, 3, 2))
        a.sort()
        assertEquals(listOf(1, 2, 3), a.iter().toList())
    }
}

private class Boxed(val value: Int) {
    override fun equals(other: Any?): Boolean = other is Boxed && other.value == value
    override fun hashCode(): Int = value.hashCode()
}

private class BoxedKey(val value: Int) : Equivalent<Boxed> {
    override fun equivalent(key: Boxed): Boolean = key.value == value
    override fun hashCode(): Int = value.hashCode()
}
