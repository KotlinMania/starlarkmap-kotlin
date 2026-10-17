// port-lint: tests sorted_map.rs
package io.github.kotlinmania.starlarkmap.sortedmap

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

import io.github.kotlinmania.serdecore.de.I32Deserialize
import io.github.kotlinmania.serdecore.de.StringDeserialize
import io.github.kotlinmania.serdejson.fromStr
import io.github.kotlinmania.serdejson.toStr
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortedMapTest {
    @Test
    fun testFromIter() {
        val map = SortedMap.fromIterator(listOf(Pair(1, 2), Pair(5, 6), Pair(3, 4)))
        assertEquals(
            listOf(Pair(1, 2), Pair(3, 4), Pair(5, 6)),
            map.iter().toList(),
        )
    }

    @Test
    fun testValueModification() {
        val map =
            SortedMap.fromIterator(
                listOf(
                    Pair(1, mutableListOf(1, 2, 3)),
                    Pair(2, mutableListOf(4)),
                    Pair(3, mutableListOf(5)),
                ),
            )
        val keys = map.keys().toMutableList()
        keys.sort()
        assertEquals(keys, map.keys().toList())
        // Support insertion for existing keys
        map.get(1)!!.add(11)
        map.get(2)!!.add(22)
        map.get(3)!!.add(33)

        assertEquals(
            listOf(
                Pair(1, mutableListOf(1, 2, 3, 11)),
                Pair(2, mutableListOf(4, 22)),
                Pair(3, mutableListOf(5, 33)),
            ),
            map.iter().toList(),
        )
        val keys2 = map.keys().toMutableList()
        keys2.sort()
        assertEquals(map.keys().toList(), keys2)
    }

    @Test
    fun testSerde() {
        val map = sortedMapOf(Pair("b", 2), Pair("a", 1))
        val serialized = toStr(map).getOrThrow()
        assertEquals("""{"a":1,"b":2}""", serialized)
        val deserialized = fromStr(serialized, SortedMap.deserialize(StringDeserialize, I32Deserialize)).getOrThrow()
        assertEquals(map, deserialized)
    }

    @Test
    fun testCompareTo() {
        val map1 = sortedMapOf(Pair("a", 1), Pair("b", 2))
        val map2 = sortedMapOf(Pair("a", 1), Pair("b", 2))
        val map3 = sortedMapOf(Pair("a", 1), Pair("b", 3))
        assertEquals(0, map1.compareTo(map2))
        assertTrue(map1.compareTo(map3) < 0)
    }
}
