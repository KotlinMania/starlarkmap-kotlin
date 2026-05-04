// port-lint: source hasher.rs
package io.github.kotlinmania.starlarkmap

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

/**
 * A hasher used by Starlark implementation.
 *
 * Starlark relies on stable hashing, and this is the hasher.
 */
class StarlarkHasher {
    companion object {
        /**
         * Creates a new hasher.
         */
        fun new(): StarlarkHasher {
            return StarlarkHasher()
        }
    }

    private val inner: FxHasher64 = FxHasher64()

    /**
     * Finish the hash computation and return the result.
     */
    fun finish(): ULong {
        return inner.finish()
    }

    /**
     * Finish the hash computation and return the lower 32 bits as a [StarlarkHashValue].
     */
    fun finishSmall(): StarlarkHashValue {
        // NOTE: Here we throw away half the key material we are given,
        // taking only the lower 32 bits.
        // Not a problem because `DefaultHasher` produces well-swizzled bits.
        return StarlarkHashValue.newUnchecked(finish().toUInt())
    }

    fun write(bytes: ByteArray) {
        inner.write(bytes)
    }

    fun writeU8(i: UByte) {
        inner.writeU8(i)
    }

    fun writeU16(i: UShort) {
        inner.writeU16(i)
    }

    fun writeU32(i: UInt) {
        inner.writeU32(i)
    }

    fun writeU64(i: ULong) {
        inner.writeU64(i)
    }

    fun writeU128(i: U128) {
        inner.writeU128(i)
    }

    fun writeUsize(i: ULong) {
        inner.writeUsize(i)
    }
}

/**
 * `BuildHasher` implementation which produces [StarlarkHasher].
 */
class StarlarkHasherBuilder {
    fun buildHasher(): StarlarkHasher {
        return StarlarkHasher()
    }
}
