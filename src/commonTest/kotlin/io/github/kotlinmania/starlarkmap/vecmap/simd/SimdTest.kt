@file:OptIn(kotlin.ExperimentalUnsignedTypes::class)

package io.github.kotlinmania.starlarkmap.vecmap.simd

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

class SimdTest {
    @Test
    fun testFindHashInArray() {
        assertEquals(null, findHashInArray(UIntArray(0), 77u))

        for (len in 1 until 20) {
            val array = UIntArray(len) { 88u }
            assertEquals(null, findHashInArray(array, 77u))

            for (i in 0 until len) {
                val arr = UIntArray(len) { j -> if (j == i) 77u else 88u }
                assertEquals(i, findHashInArray(arr, 77u))
            }

            for (i in 0 until len) {
                // Test first index is returned if there are multiple matches.
                val arr = UIntArray(len) { j -> if (j < i) 88u else 77u }
                assertEquals(i, findHashInArray(arr, 77u))
            }
        }
    }
}
