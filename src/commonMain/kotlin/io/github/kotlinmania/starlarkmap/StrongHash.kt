// port-lint: source strong_hash (external crate)
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.starlarkmap

import kotlin.native.HiddenFromObjC

/*
 * Copyright 2019 The Starlark in Rust Authors.
 * Copyright (c) Facebook, Inc. and its affiliates.
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

/**
 * A trait similar to hashing, but with the expectation that the hash produced should be as
 * perturbed as possible.
 */
// SAM by design: its generated constructor takes a `(StarlarkHasher) -> Unit` closure, which the
// Kotlin/Native Swift-export cache builder cannot lower. Hidden from the Swift bridge; Kotlin
// implementors are unaffected.
@HiddenFromObjC
fun interface StrongHash {
    fun strongHash(state: StarlarkHasher)
}

