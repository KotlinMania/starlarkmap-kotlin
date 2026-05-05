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
class StarlarkHasher : Hasher {
    companion object {
        /**
         * Creates a new hasher.
         */
        fun new(): StarlarkHasher {
            return StarlarkHasher()
        }
    }

    // TODO(nga): `FxHasher64` is endian-dependent, this is not right.
    private val inner: FxHasher64 = FxHasher64()

    /**
     * Finish the hash computation and return the result.
     */
    fun finishSmall(): StarlarkHashValue {
        // NOTE: Here we throw away half the key material we are given,
        // taking only the lower 32 bits.
        // Not a problem because the default hasher produces well-swizzled bits.
        return StarlarkHashValue.newUnchecked(finish().toUInt())
    }

    override fun finish(): ULong {
        return inner.finish()
    }

    override fun write(bytes: ByteArray) {
        inner.write(bytes)
    }

    override fun writeU8(i: UByte) {
        inner.writeU8(i)
    }

    override fun writeU16(i: UShort) {
        inner.writeU16(i)
    }

    override fun writeU32(i: UInt) {
        inner.writeU32(i)
    }

    override fun writeU64(i: ULong) {
        inner.writeU64(i)
    }

    override fun writeU128(i: U128) {
        inner.writeU128(i)
    }

    override fun writeUsize(i: ULong) {
        inner.writeUsize(i)
    }
}

/**
 * `BuildHasher` implementation which produces [StarlarkHasher].
 */
class StarlarkHasherBuilder : BuildHasher<StarlarkHasher> {
    /**
     * Create a new hasher.
     */
    override fun buildHasher(): StarlarkHasher {
        return StarlarkHasher()
    }
}

interface Hasher {
    fun finish(): ULong
    fun write(bytes: ByteArray)
    fun writeU8(i: UByte)
    fun writeU16(i: UShort)
    fun writeU32(i: UInt)
    fun writeU64(i: ULong)
    fun writeU128(i: U128)
    fun writeUsize(i: ULong)
}

interface BuildHasher<H : Hasher> {
    fun buildHasher(): H
}
