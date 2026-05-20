// port-lint: source vec_map/iter.rs
package io.github.kotlinmania.starlarkmap.vecmap.iter

import io.github.kotlinmania.starlarkmap.Hashed
import io.github.kotlinmania.starlarkmap.StarlarkHashValue

internal class Keys<K, V>(
    internal val iter: Iter<K, V>,
) : Iterator<K> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): K = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<K, V>): K {
        val (key, _value) = item
        return key
    }

    private fun remaining(): Int = iter.len()
}

internal class Values<K, V>(
    internal val iter: Iter<K, V>,
) : Iterator<V> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): V = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<K, V>): V {
        val (_key, value) = item
        return value
    }

    private fun remaining(): Int = iter.len()
}

internal class ValuesMut<K, V>(
    internal val iter: IterMut<K, V>,
) : Iterator<V> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): V = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<K, V>): V {
        val (_key, value) = item
        return value
    }

    private fun remaining(): Int = iter.len()
}

internal class Iter<K, V>(
    internal val iter: Iterator<Pair<K, V>>,
    private val remaining: () -> Int,
) : Iterator<Pair<K, V>> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): Pair<K, V> = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<K, V>): Pair<K, V> {
        val (key, value) = item
        return Pair(key, value)
    }
}

internal class IterHashed<K, V>(
    internal val iter: Iterator<Pair<Pair<K, V>, StarlarkHashValue>>,
    private val remaining: () -> Int,
) : Iterator<Pair<Hashed<K>, V>> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): Pair<Hashed<K>, V> = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<Pair<K, V>, StarlarkHashValue>): Pair<Hashed<K>, V> {
        val (pair, hash) = item
        val (key, value) = pair
        return Pair(Hashed.newUnchecked(hash, key), value)
    }
}

internal class IterMut<K, V>(
    internal val iter: Iterator<Pair<K, V>>,
    private val remaining: () -> Int,
) : Iterator<Pair<K, V>> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): Pair<K, V> = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<K, V>): Pair<K, V> {
        val (key, value) = item
        return Pair(key, value)
    }
}

internal class IterMutUnchecked<K, V>(
    internal val iter: Iterator<Pair<K, V>>,
    private val remaining: () -> Int,
) : Iterator<Pair<K, V>> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): Pair<K, V> = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<K, V>): Pair<K, V> {
        val (key, value) = item
        return Pair(key, value)
    }
}

internal class IntoIterHashed<K, V>(
    internal val iter: Iterator<Pair<Pair<K, V>, StarlarkHashValue>>,
    private val remaining: () -> Int,
) : Iterator<Pair<Hashed<K>, V>> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): Pair<Hashed<K>, V> = map(iter.next())
    fun len(): Int = remaining()

    private fun map(item: Pair<Pair<K, V>, StarlarkHashValue>): Pair<Hashed<K>, V> {
        val (pair, hash) = item
        val (key, value) = pair
        return Pair(Hashed.newUnchecked(hash, key), value)
    }
}

internal class IntoIter<K, V>(
    internal val iter: IntoIterHashed<K, V>,
) : Iterator<Pair<K, V>> {
    override fun hasNext(): Boolean = iter.hasNext()
    override fun next(): Pair<K, V> = map(iter.next())
    fun len(): Int = iter.len()

    private fun map(item: Pair<Hashed<K>, V>): Pair<K, V> {
        val (key, value) = item
        return Pair(key.intoKey(), value)
    }
}
