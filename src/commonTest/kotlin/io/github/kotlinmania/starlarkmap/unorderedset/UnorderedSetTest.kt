// port-lint: source tests:src/unordered_set.rs
package io.github.kotlinmania.starlarkmap.unorderedset

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
import kotlin.test.assertTrue

class UnorderedSetTest {
    @Test
    fun testInsert() {
        val set = UnorderedSet.new<Int>()
        assertTrue(set.insert(10))
        assertFalse(set.insert(10))
        assertTrue(set.insert(20))
        assertFalse(set.insert(20))
        assertEquals(2, set.len())
    }

    @Test
    fun testEntriesSorted() {
        val set = UnorderedSet.new<Int>()
        set.insert(20)
        set.insert(10)
        set.insert(30)
        assertEquals(listOf(10, 20, 30), set.entriesSorted())
    }
}
