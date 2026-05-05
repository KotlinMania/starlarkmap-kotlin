// port-lint: source hashed.rs
package io.github.kotlinmania.starlarkmap

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
 * A key and its hash.
 */
class Hashed<K> private constructor(
    internal val hash: StarlarkHashValue,
    internal val key: K,
) {
    companion object {
        /**
         * Create a new [Hashed] value using the weak hash of the key.
         */
        fun <K> new(key: K): Hashed<K> = newUnchecked(StarlarkHashValue.new(key), key)

        /**
         * Directly create a new [Hashed] using a given hash value.
         * If the hash does not correspond to the key, its will cause issues.
         */
        fun <K> newUnchecked(hash: StarlarkHashValue, key: K): Hashed<K> = Hashed(hash, key)
    }

    /**
     * Get the underlying key.
     */
    fun key(): K = key

    /**
     * Get the underlying key, as mutable.
     */
    fun keyMut(): K = key

    /**
     * Get the underlying key taking ownership.
     */
    fun intoKey(): K = key

    fun deref(): K = key

    fun fmt(f: Appendable): Appendable {
        val key = this.key
        return key.fmt(f)
    }

    /**
     * Get the underlying hash.
     */
    fun hash(): StarlarkHashValue = hash

    // We deliberately know that this is a hash and value, so our equals/hashCode are fine.
    fun hash(state: StarlarkHasher) {
        // Only hash the hash, not the key.
        state.writeU32(hash.get())
    }

    /**
     * Convert [Hashed] holding a key into [Hashed] holding an equivalent key reference.
     *
     * Kotlin does not have borrowing, so this returns a [Hashed] with the same key value.
     */
    fun asRef(): Hashed<K> {
        val hash = this.hash
        val key = this.key
        return newUnchecked(hash, key)
    }

    /**
     * Make [Hashed] from [Hashed] holding an equivalent key reference.
     *
     * Kotlin does not have borrowing, so this returns a [Hashed] with the same key value.
     */
    fun copied(): Hashed<K> {
        val hash = this.hash
        val key = this.key
        return newUnchecked(hash, key)
    }

    /**
     * Make [Hashed] from [Hashed] holding an equivalent key reference, where `K` is cloneable.
     *
     * Kotlin does not have borrowing, so the clone function is applied to the stored key.
     */
    fun cloned(clone: (K) -> K): Hashed<K> {
        val hash = this.hash
        val key = clone(this.key)
        return newUnchecked(hash, key)
    }

    /**
     * Make [Hashed] from [Hashed] holding an equivalent key reference, where `T` is the owned form of `K`.
     *
     * Kotlin does not have borrowing, so the `toOwned` function is applied to the stored key.
     */
    fun <T> owned(toOwned: (K) -> T): Hashed<T> {
        val hash = this.hash
        val key = toOwned(this.key)
        return newUnchecked(hash, key)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Hashed<*>) return false
        return hash == other.hash && key == other.key
    }

    override fun hashCode(): Int {
        // Only hash the hash, not the key.
        return hash.hashCode()
    }

    override fun toString(): String {
        return key.toString()
    }
}

private fun Any?.fmt(f: Appendable): Appendable {
    f.append(this.toString())
    return f
}


fun <K : StrongHash> Hashed<K>.strongHash(state: StarlarkHasher) {
    key().strongHash(state)
}

fun <K : Comparable<K>> Hashed<K>.partialCmp(other: Hashed<K>): Int? {
    return key().compareTo(other.key())
}

fun <K : Comparable<K>> Hashed<K>.cmp(other: Hashed<K>): Int {
    return key().compareTo(other.key())
}
