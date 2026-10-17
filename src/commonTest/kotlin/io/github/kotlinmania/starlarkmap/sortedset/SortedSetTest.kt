// port-lint: tests sorted_set.rs
package io.github.kotlinmania.starlarkmap.sortedset

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

import io.github.kotlinmania.serdecore.de.StringDeserialize
import io.github.kotlinmania.serdejson.fromStr
import io.github.kotlinmania.serdejson.toStr
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortedSetTest {
    @Test
    fun testFromIterAndOrder() {
        val set = SortedSet.fromIter(listOf("c", "a", "b"))
        assertEquals(3, set.len())
        assertEquals(listOf("a", "b", "c"), set.iter().toList())
        assertEquals("a", set.getIndex(0))
        assertEquals("b", set.getIndex(1))
        assertEquals("c", set.getIndex(2))
    }

    @Test
    fun testContainsAndGet() {
        val set = sortedSetOf("x", "y", "z")
        assertTrue(set.contains("y"))
        assertEquals("y", set.get("y"))
        assertEquals(false, set.contains("w"))
        assertEquals(null, set.get("w"))
    }

    @Test
    fun testSerde() {
        val set = sortedSetOf("b", "a")
        val serialized = toStr(set).getOrThrow()
        assertEquals("""["a","b"]""", serialized)
        val deserialized = fromStr(serialized, SortedSet.deserialize(StringDeserialize)).getOrThrow()
        assertEquals(set, deserialized)
    }

    @Test
    fun testCompareTo() {
        val set1 = sortedSetOf("a", "b")
        val set2 = sortedSetOf("a", "b")
        val set3 = sortedSetOf("a", "c")
        assertEquals(0, set1.compareTo(set2))
        assertTrue(set1.compareTo(set3) < 0)
    }
}
