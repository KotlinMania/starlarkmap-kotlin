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

class SmallSetTest {

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
}
