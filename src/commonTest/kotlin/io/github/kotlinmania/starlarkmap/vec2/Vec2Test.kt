// port-lint: source tests:src/vec2.rs
package io.github.kotlinmania.starlarkmap.vec2

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
import kotlin.test.assertNull

class Vec2Test {
    @Test
    fun testPush() {
        val v = Vec2.new<Int, Int>()
        v.push(1, 2)
        assertEquals(1, v.len())
        assertEquals(Pair(1, 2), v.get(0))
    }

    @Test
    fun testPushMany() {
        val v = Vec2.new<String, Int>()
        for (i in 0 until 100) {
            v.push(i.toString(), i * 2)
        }
        assertEquals(100, v.len())
        for (i in 0 until 100) {
            assertEquals(Pair(i.toString(), i * 2), v.get(i))
        }
    }

    @Test
    fun testIntoIter() {
        val v = Vec2.new<String, Int>()
        for (i in 0 until 100) {
            v.push(i.toString(), i * 2)
        }
        for ((i, pair) in v.iter().withIndex()) {
            val (a, b) = pair
            assertEquals(i.toString(), a)
            assertEquals(i * 2, b)
        }
    }

    @Test
    fun testSortInsertionBy() {
        val v = Vec2.new<Int, Int>()
        v.push(1, 2)
        v.push(3, 4)
        v.push(2, 3)
        v.push(3, 2)
        v.sortInsertionBy { x, y ->
            val cmp = x.first.compareTo(y.first)
            if (cmp != 0) cmp else x.second.compareTo(y.second)
        }
        assertEquals(Pair(1, 2), v.get(0))
        assertEquals(Pair(2, 3), v.get(1))
        assertEquals(Pair(3, 2), v.get(2))
        assertEquals(Pair(3, 4), v.get(3))
    }

    @Test
    fun testShrinkToFit() {
        val v = Vec2.withCapacity<String, String>(10)
        v.push("a", "b")
        v.push("c", "d")
        v.shrinkToFit()
        for (unused in 0 until 2) {
            assertEquals(2, v.len())
            assertEquals(2, v.capacity())
            assertEquals(
                listOf(Pair("a", "b"), Pair("c", "d")),
                v.iter().toList(),
            )
        }
    }

    @Test
    fun testTruncate() {
        val v = Vec2.new<Int, Int>()
        v.push(0, 100)
        v.push(200, 300)
        v.push(400, 500)
        v.truncate(1)
        assertEquals(1, v.len())
        assertEquals(Pair(0, 100), v.get(0))
        assertNull(v.get(1))
    }

    @Test
    fun testRetain() {
        val v = Vec2.new<Int, Int>()
        v.push(1, 2)
        v.push(2, 3)
        v.push(3, 4)
        var sawTwo = false
        v.retain { a, b ->
            if (a == 2) {
                assertEquals(3, b)
                sawTwo = true
                false
            } else {
                true
            }
        }
        assertEquals(true, sawTwo)
        assertEquals(2, v.len())
        assertEquals(Pair(1, 2), v.get(0))
        assertEquals(Pair(3, 4), v.get(1))
    }

    @Test
    fun testFirst() {
        val v: Vec2<Int, Int> = Vec2.new()
        assertNull(v.first())
        v.push(1, 2)
        assertEquals(Pair(1, 2), v.first())
        v.push(3, 4)
        assertEquals(Pair(1, 2), v.first())
        assertFalse(v.isEmpty())
    }

    @Test
    fun testLast() {
        val v: Vec2<Int, Int> = Vec2.new()
        assertNull(v.last())
        v.push(1, 2)
        assertEquals(Pair(1, 2), v.last())
        v.push(3, 4)
        assertEquals(Pair(3, 4), v.last())
    }
}
