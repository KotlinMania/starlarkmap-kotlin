# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Current Progress:** 92.0% (35/25 files)
- **Matched Files:** 23
- **Average Similarity:** 0.44
- **Critical Issues:** 16 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. hashed
- **Similarity:** 0.67 (needs 18% improvement)
- **Dependencies:** 10
- **Priority Score:** 10011703.0
- **Functions:** 15/15 matched (target 19)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`
- **Symbol Deficit:** 1 (functions: 0, types: 1)
- **Action:** Review and complete missing sections

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. hashed

- **Target:** `starlarkmap.Hashed`
- **Similarity:** 0.67
- **Dependents:** 10
- **Priority Score:** 10011703.0
- **Functions:** 15/15 matched (target 19)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`

### 2. hasher

- **Target:** `starlarkmap.Hasher`
- **Similarity:** 0.73
- **Dependents:** 6
- **Priority Score:** 6011402.5
- **Functions:** 11/11 matched (target 25)
- **Missing functions:** _none_
- **Types:** 2/3 matched
- **Missing types:** `Hasher`

### 3. small_map

- **Target:** `smallmap.SmallMap`
- **Similarity:** 0.25
- **Dependents:** 4
- **Priority Score:** 4612007.5
- **Functions:** 58/109 matched (target 66)
- **Missing functions:** `fmt`, `assert_invariants`, `into_keys`, `into_values`, `get_full`, `get_full_hashed`, `get_index_of_hashed_raw_with_index`, `get_index_of_hashed_raw`, `get_mut_hashed`, `get_mut`, `contains_key_hashed`, `contains_key_hashed_by_value`, `contains_key`, `create_index`, `rebuild_index`, `hasher`, `entry_hashed`, `entry`, `state_check`, `drop`, `key`, `into_mut`, `into_mut_entry`, `as_key_and_mut_value`, `insert_entry`, `or_insert`, `or_insert_with`, `or_default`, `or_insert_entry_with`, `and_modify`, `eq`, `pagable_serialize`, `pagable_deserialize`, `serialize`, `deserialize`, `expecting`, `visit_map`, `empty_map`, `few_entries`, `many_entries`, `test_smallmap_macro`, `test_clone`, `test_duplicate_hashes`, `hash`, `test_smallmap_debug`, `test_sort_keys_updates_index_on_panic`, `partial_cmp`, `cmp`, `test_json`, `test_retain`, `test_and_modify`
- **Types:** 2/11 matched (target 3)
- **Missing types:** `RebuildIndexOnDrop`, `OccupiedEntry`, `VacantEntry`, `Item`, `IntoIter`, `MapVisitor`, `Value`, `K`, `Key`
- **Tests:** 12/28 matched
- **Lint issues:** 1

### 4. iter

- **Target:** `starlarkmap.Iter [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 4
- **Priority Score:** 4000010.0
- **Functions:** 0/0 matched (target 7)
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_

### 5. vec2

- **Target:** `vec2.Vec2 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.21
- **Dependents:** 3
- **Priority Score:** 3427308.0
- **Functions:** 30/63 matched (target 30)
- **Missing functions:** `new_checked`, `alloc`, `dealloc`, `pagable_serialize`, `pagable_deserialize`, `serialize`, `deserialize`, `expecting`, `visit_seq`, `default`, `fmt`, `clone`, `aaa_ptr`, `bbb_ptr`, `aaa_mut`, `aaa_uninit`, `bbb_mut`, `bbb_uninit`, `reserve_slow`, `dealloc_impl`, `drop_in_place`, `get_unchecked`, `get_mut`, `get_unchecked_mut`, `read`, `drop`, `into_iter`, `eq`, `hash`, `visit`, `test_layout_for`, `test_alloc_dealloc`, `test_alignment`
- **Types:** 1/10 matched (target 2)
- **Missing types:** `Vec2Layout`, `Vec2Visitor`, `Value`, `DropInPlace`, `Retain`, `Item`, `IntoIter`, `Align16`, `Align8`
- **Tests:** 9/12 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/vec2.rs` vs expected `vec2.rs`
- **Proposed provenance header:** `// port-lint: tests vec2.rs` (current: `// port-lint: tests src/vec2.rs`)
- **Lint issues:** 2

### 6. small_set

- **Target:** `smallset.SmallSet`
- **Similarity:** 0.34
- **Dependents:** 2
- **Priority Score:** 2257206.5
- **Functions:** 46/65 matched (target 64)
- **Missing functions:** `default`, `fmt`, `eq`, `from_iter`, `iter_mut_unchecked`, `get_index_of_hashed`, `get_index_of_hashed_by_value`, `hash_ordered`, `retain`, `into_iter`, `next`, `size_hint`, `serialize`, `deserialize`, `expecting`, `visit_seq`, `small_set_macros`, `test_difference_size_hint`, `test_json`
- **Types:** 1/7 matched (target 4)
- **Missing types:** `Item`, `IntoIter`, `Difference`, `Union`, `SeqVisitor`, `Value`
- **Tests:** 10/13 matched

### 7. vec_map

- **Target:** `vecmap.VecMap`
- **Similarity:** 0.48
- **Dependents:** 2
- **Priority Score:** 2003305.1
- **Functions:** 32/32 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Lint issues:** 1

### 8. ordered_map

- **Target:** `orderedmap.OrderedMap [PROVENANCE-FALLBACK]`
- **Similarity:** 0.35
- **Dependents:** 1
- **Priority Score:** 1163806.5
- **Functions:** 20/34 matched (target 44)
- **Missing functions:** `iter_mut`, `values_mut`, `get_mut`, `entry`, `eq`, `hash`, `strong_hash`, `partial_cmp`, `cmp`, `from_iter`, `into_iter`, `serialize`, `deserialize`, `test_serde`
- **Types:** 2/4 matched (target 3)
- **Missing types:** `Item`, `IntoIter`
- **Tests:** 1/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/orderedMap.rs` vs expected `ordered_map.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/orderedMap.rs` vs expected `ordered_map.rs`
- **Proposed provenance header:** `// port-lint: source ordered_map.rs` (current: `// port-lint: source src/orderedMap.rs`)
- **Proposed provenance header:** `// port-lint: tests ordered_map.rs` (current: `// port-lint: tests src/orderedMap.rs`)
- **Lint issues:** 2

### 9. unordered_map

- **Target:** `unorderedmap.UnorderedMap [PROVENANCE-FALLBACK]`
- **Similarity:** 0.31
- **Dependents:** 1
- **Priority Score:** 1145306.9
- **Functions:** 31/44 matched (target 49)
- **Missing functions:** `get_hashed`, `get_mut`, `entries_unordered_mut`, `values_unordered_mut`, `into_entries_unordered`, `index`, `fmt`, `eq`, `hash`, `from_iter`, `pagable_serialize`, `pagable_deserialize`, `key_mut`
- **Types:** 8/9 matched (target 11)
- **Missing types:** `Output`
- **Tests:** 5/5 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/unorderedMap.rs` vs expected `unordered_map.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/unordered_map.rs` vs expected `unordered_map.rs`
- **Proposed provenance header:** `// port-lint: source unordered_map.rs` (current: `// port-lint: source src/unorderedMap.rs`)
- **Proposed provenance header:** `// port-lint: tests unordered_map.rs` (current: `// port-lint: tests src/unordered_map.rs`)
- **Lint issues:** 2

### 10. ordered_set

- **Target:** `orderedset.OrderedSet [PROVENANCE-FALLBACK]`
- **Similarity:** 0.57
- **Dependents:** 1
- **Priority Score:** 1093604.2
- **Functions:** 24/30 matched (target 39)
- **Missing functions:** `eq`, `partial_cmp`, `cmp`, `hash`, `into_iter`, `from_iter`
- **Types:** 3/6 matched (target 5)
- **Missing types:** `Iter`, `IntoIter`, `Item`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/orderedSet.rs` vs expected `ordered_set.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/ordered_set.rs` vs expected `ordered_set.rs`
- **Proposed provenance header:** `// port-lint: source ordered_set.rs` (current: `// port-lint: source src/orderedSet.rs`)
- **Proposed provenance header:** `// port-lint: tests ordered_set.rs` (current: `// port-lint: tests src/ordered_set.rs`)
- **Lint issues:** 2

### 11. sorted_vec

- **Target:** `sortedvec.SortedVec [PROVENANCE-FALLBACK]`
- **Similarity:** 0.62
- **Dependents:** 1
- **Priority Score:** 1031003.8
- **Functions:** 6/7 matched (target 12)
- **Missing functions:** `test_new_unchecked`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Item`, `IntoIter`
- **Tests:** 0/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/sortedVec.rs` vs expected `sorted_vec.rs`
- **Proposed provenance header:** `// port-lint: source sorted_vec.rs` (current: `// port-lint: source src/sortedVec.rs`)
- **Lint issues:** 1

### 12. mix_u32

- **Target:** `starlarkmap.MixU32 [PROVENANCE-FALLBACK]`
- **Similarity:** 0.27
- **Dependents:** 1
- **Priority Score:** 1000107.2
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/mixU32.rs` vs expected `mix_u32.rs`
- **Proposed provenance header:** `// port-lint: source mix_u32.rs` (current: `// port-lint: source src/mixU32.rs`)
- **Lint issues:** 1

### 13. small_map.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.smallmap.iter.Iter`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 122407.6
- **Functions:** 1/12 matched (target 30)
- **Missing functions:** `map`, `_assert_iterators_sync_send`, `assert_sync_send`, `test_iter_hashed`, `test_iter`, `test_into_iter_hashed`, `test_into_iter`, `test_keys`, `test_values`, `test_into_keys`, `test_into_values`
- **Types:** 11/12 matched (target 11)
- **Missing types:** `Item`

### 14. sorted_map

- **Target:** `sortedmap.SortedMap [PROVENANCE-FALLBACK]`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 92306.1
- **Functions:** 13/20 matched (target 21)
- **Missing functions:** `iter_mut`, `values_mut`, `get_mut`, `from_iter`, `into_iter`, `serialize`, `deserialize`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Item`, `IntoIter`
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/sortedMap.rs` vs expected `sorted_map.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/sorted_map.rs` vs expected `sorted_map.rs`
- **Proposed provenance header:** `// port-lint: source sorted_map.rs` (current: `// port-lint: source src/sortedMap.rs`)
- **Proposed provenance header:** `// port-lint: tests sorted_map.rs` (current: `// port-lint: tests src/sorted_map.rs`)
- **Lint issues:** 2

### 15. vec_map.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.vecmap.iter.Iter [PROVENANCE-FALLBACK]`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 51209.4
- **Functions:** 1/2 matched (target 16)
- **Missing functions:** `map`
- **Types:** 6/10 matched (target 6)
- **Missing types:** `Item`, `ValuesMut`, `IterMut`, `IterMutUnchecked`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/vec_map/iter.rs` vs expected `vec_map/iter.rs`
- **Proposed provenance header:** `// port-lint: source vec_map/iter.rs` (current: `// port-lint: source src/vec_map/iter.rs`)
- **Lint issues:** 1

### 16. sorted_set

- **Target:** `sortedset.SortedSet [PROVENANCE-FALLBACK]`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 41602.7
- **Functions:** 11/13 matched (target 20)
- **Missing functions:** `from_iter`, `into_iter`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Item`, `IntoIter`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/sortedSet.rs` vs expected `sorted_set.rs`
- **Proposed provenance header:** `// port-lint: source sorted_set.rs` (current: `// port-lint: source src/sortedSet.rs`)
- **Lint issues:** 1

### 17. sorting.insertion

- **Target:** `insertion.Insertion`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 40706.3
- **Functions:** 3/7 matched (target 3)
- **Missing functions:** `test_find_insertion_point`, `find_insertion_point_ints`, `test_insertion_sort`, `insertion_sort_ints`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Tests:** 0/4 matched

### 18. small_set.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.smallset.iter.Iter`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 30905.6
- **Functions:** 1/3 matched (target 15)
- **Missing functions:** `clone`, `map`
- **Types:** 5/6 matched (target 5)
- **Missing types:** `Item`

### 19. unordered_set

- **Target:** `unorderedset.UnorderedSet [PROVENANCE-FALLBACK]`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 22603.5
- **Functions:** 19/21 matched (target 26)
- **Missing functions:** `eq`, `from_iter`
- **Types:** 5/5 matched (target 8)
- **Missing types:** _none_
- **Tests:** 2/2 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/unorderedSet.rs` vs expected `unordered_set.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `tests:src/unordered_set.rs` vs expected `unordered_set.rs`
- **Proposed provenance header:** `// port-lint: source unordered_set.rs` (current: `// port-lint: source src/unorderedSet.rs`)
- **Proposed provenance header:** `// port-lint: tests unordered_set.rs` (current: `// port-lint: tests src/unordered_set.rs`)
- **Lint issues:** 2

### 20. vec2.iter

- **Target:** `iter.Iter`
- **Similarity:** 0.31
- **Dependents:** 0
- **Priority Score:** 20806.9
- **Functions:** 4/5 matched (target 10)
- **Missing functions:** `drop`
- **Types:** 2/3 matched (target 2)
- **Missing types:** `Item`

### 21. vec_map.simd

- **Target:** `simd.Simd`
- **Similarity:** 0.49
- **Dependents:** 0
- **Priority Score:** 20405.1
- **Functions:** 2/3 matched (target 2)
- **Missing functions:** `test_find_hash_in_array`
- **Types:** 0/1 matched (target 0)
- **Missing types:** `T`
- **Tests:** 0/1 matched

### 22. hash_value

- **Target:** `starlarkmap.HashValue [PROVENANCE-FALLBACK]`
- **Similarity:** 0.63
- **Dependents:** 0
- **Priority Score:** 603.7
- **Functions:** 5/5 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/hashValue.rs` vs expected `hash_value.rs`
- **Proposed provenance header:** `// port-lint: source hash_value.rs` (current: `// port-lint: source src/hashValue.rs`)
- **Lint issues:** 1

### 23. vec_map.hint

- **Target:** `hint.Hint`
- **Similarity:** 0.95
- **Dependents:** 0
- **Priority Score:** 100.5
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance

# Get next high-priority task
```
## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |
| `sorting` | `sorting.Sorting` | 0 | `sorting.rs` | `sorting/Sorting.kt` |
