# starlarkmap-kotlin

Kotlin Multiplatform port of [`starlark_map`](https://github.com/facebook/starlark-rust/tree/main/starlark_map),
the supporting map/set/vec crate from Facebook's `starlark-rust`. Used by `starlark-kotlin`
and any other consumer that needs `SmallMap`, `OrderedMap`, `SortedMap`, `UnorderedMap`,
`Vec2`, `Hashed`, etc. with the same semantics as upstream.

Upstream: <https://github.com/facebook/starlark-rust>

## Status

Build infrastructure scaffolding only. Source ports live under
`src/commonMain/kotlin/io/github/kotlinmania/starlarkmap/` and are added file-by-file
as upstream `tmp/starlark_map/src/<module>.rs` files are translated.

## Namespace

```
io.github.kotlinmania.starlarkmap
io.github.kotlinmania.starlarkmap.smallmap
io.github.kotlinmania.starlarkmap.smallset
io.github.kotlinmania.starlarkmap.sortedmap
io.github.kotlinmania.starlarkmap.sortedset
io.github.kotlinmania.starlarkmap.sortedvec
io.github.kotlinmania.starlarkmap.unorderedmap
io.github.kotlinmania.starlarkmap.unorderedset
io.github.kotlinmania.starlarkmap.vec2
```

## Build

```bash
./gradlew build
./gradlew test
./gradlew jvmTest
./gradlew macosArm64Test
```

## Maven coordinates

```
io.github.kotlinmania:starlarkmap:0.1.0
```

## License

Apache-2.0. See [LICENSE](./LICENSE).
