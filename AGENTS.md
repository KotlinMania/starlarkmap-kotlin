# Agent Porting Guide — starlarkmap-kotlin

This document covers patterns and conventions for porting `starlark_map` (Rust) to Kotlin Multiplatform.

## Source of truth

Upstream Rust source: `tmp/starlark_map/` (mirrored from `facebook/starlark-rust/starlark_map`).

Add the upstream as a sibling clone under `tmp/` before starting work:

```bash
git clone --depth=1 https://github.com/facebook/starlark-rust.git tmp/starlark-rust
ln -s starlark-rust/starlark_map tmp/starlark_map
```

## Rust → Kotlin Mappings

| Rust | Kotlin |
|---|---|
| `Vec<T>` | `MutableList<T>` / `List<T>` |
| `HashMap<K, V>` | `MutableMap<K, V>` (LinkedHashMap for insertion order) |
| `BTreeMap<K, V>` | `sortedMapOf()` / kotlinx-collections-immutable |
| `Option<T>` | `T?` |
| `Result<T, E>` | `Result<T>` (exception-carrying) |
| `Box<T>` | plain reference (Kotlin GC) |
| `Rc<T>` / `Arc<T>` | plain reference; atomic refs only when interior mutability needed |
| `RefCell<T>` | mutable property |
| Trait | Interface |
| Enum with data | Sealed class |
| Struct | Data class (or class with internal fields) |
| `pub(crate)` | `internal` |
| `pub(super)` | `internal` (closest equivalent) |

## File organization

```
src/
├── commonMain/kotlin/io/github/kotlinmania/starlarkmap/
│   ├── HashValue.kt         # port of src/hash_value.rs
│   ├── Hashed.kt            # port of src/hashed.rs
│   ├── Hasher.kt            # port of src/hasher.rs
│   ├── Iter.kt              # port of src/iter.rs
│   ├── MixU32.kt            # port of src/mix_u32.rs
│   ├── OrderedMap.kt        # port of src/ordered_map.rs
│   ├── OrderedSet.kt        # port of src/ordered_set.rs
│   ├── SmallMap.kt          # port of src/small_map.rs
│   ├── SmallSet.kt          # port of src/small_set.rs
│   ├── SortedMap.kt         # port of src/sorted_map.rs
│   ├── SortedSet.kt         # port of src/sorted_set.rs
│   ├── SortedVec.kt         # port of src/sorted_vec.rs
│   ├── Sorting.kt           # port of src/sorting.rs
│   ├── UnorderedMap.kt      # port of src/unordered_map.rs
│   ├── UnorderedSet.kt      # port of src/unordered_set.rs
│   ├── Vec2.kt              # port of src/vec2.rs
│   ├── VecMap.kt            # port of src/vec_map.rs
│   ├── smallmap/            # port of src/small_map/
│   ├── smallset/            # port of src/small_set/
│   ├── sorting/             # port of src/sorting/
│   ├── vec2/                # port of src/vec2/
│   └── vecmap/              # port of src/vec_map/
└── commonTest/kotlin/io/github/kotlinmania/starlarkmap/
```

## Porting order (leaves first)

1. `hasher.rs`, `hash_value.rs`, `mix_u32.rs` — primitive hashing infrastructure
2. `hashed.rs` — pre-hashed key wrapper
3. `iter.rs` — iterator helpers
4. `sorting.rs` — sort algorithms
5. `vec2.rs`, `vec_map.rs` — backing storage types
6. `small_map.rs`, `small_set.rs` — main collections
7. `ordered_map.rs`, `ordered_set.rs`
8. `sorted_map.rs`, `sorted_set.rs`, `sorted_vec.rs`
9. `unordered_map.rs`, `unordered_set.rs`

## Verification

Once `tools/ast_distance/ast_distance` is wired up:

```bash
./tools/ast_distance/ast_distance \
  tmp/starlark_map/src/small_map.rs rust \
  src/commonMain/kotlin/io/github/kotlinmania/starlarkmap/SmallMap.kt kotlin
```

Target: similarity ≥ 0.85.
