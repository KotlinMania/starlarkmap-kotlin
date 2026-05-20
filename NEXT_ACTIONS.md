# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 23/25 (92.0%)
- **Function parity:** 366/503 matched (target 614) — 72.8%
- **Class/type parity:** 64/100 matched (target 86) — 64.0%
- **Combined symbol parity:** 430/603 matched (target 700) — 71.3%
- **Average inline-code cosine:** 0.48 (function body across 23 matched files)
- **Average documentation cosine:** 0.58 (doc text across 23 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 15 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. hashed

- **Target:** `starlarkmap.Hashed`
- **Similarity:** 0.86
- **Dependents:** 10
- **Priority Score:** 10011701.0
- **Functions:** 15/15 matched (target 20)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`

### 2. hasher

- **Target:** `starlarkmap.Hasher`
- **Similarity:** 0.76
- **Dependents:** 6
- **Priority Score:** 6001402.5
- **Functions:** 11/11 matched
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_
- **TODOs:** 1

### 3. small_map

- **Target:** `smallmap.SmallMap`
- **Similarity:** 0.50
- **Dependents:** 4
- **Priority Score:** 4312005.0
- **Functions:** 84/109 matched (target 111)
- **Missing functions:** `fmt`, `assert_invariants`, `hasher`, `insert_unique_unchecked`, `state_check`, `is_sorted_by_key`, `drop`, `into_mut`, `into_mut_entry`, `as_key_and_mut_value`, `insert_entry`, `or_default`, `or_insert_entry_with`, `eq`, `pagable_serialize`, `pagable_deserialize`, `serialize`, `deserialize`, `expecting`, `visit_map`, `hash`, `partial_cmp`, `cmp`, `test_json`, `test_retain`
- **Types:** 6/11 matched
- **Missing types:** `RebuildIndexOnDrop`, `Item`, `IntoIter`, `MapVisitor`, `Value`
- **Tests:** 21/28 matched

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

- **Target:** `vec2.Vec2`
- **Similarity:** 0.24
- **Dependents:** 3
- **Priority Score:** 3427307.8
- **Functions:** 30/63 matched (target 38)
- **Missing functions:** `new_checked`, `alloc`, `dealloc`, `pagable_serialize`, `pagable_deserialize`, `serialize`, `deserialize`, `expecting`, `visit_seq`, `default`, `fmt`, `clone`, `aaa_ptr`, `bbb_ptr`, `aaa`, `aaa_mut`, `aaa_uninit`, `bbb`, `bbb_mut`, `bbb_uninit`, `reserve_slow`, `dealloc_impl`, `drop_in_place`, `get_mut`, `get_unchecked_mut`, `read`, `drop`, `eq`, `hash`, `visit`, `test_layout_for`, `test_alloc_dealloc`, `test_alignment`
- **Types:** 1/10 matched (target 2)
- **Missing types:** `Vec2Layout`, `Vec2Visitor`, `Value`, `DropInPlace`, `Retain`, `Item`, `IntoIter`, `Align16`, `Align8`
- **Tests:** 9/12 matched
- **Lint issues:** 1

### 6. small_set

- **Target:** `smallset.SmallSet`
- **Similarity:** 0.43
- **Dependents:** 2
- **Priority Score:** 2197205.8
- **Functions:** 50/65 matched (target 72)
- **Missing functions:** `default`, `fmt`, `eq`, `from_iter`, `iter_mut_unchecked`, `get_index_of_hashed`, `get_index_of_hashed_by_value`, `hash_ordered`, `retain`, `into_iter`, `serialize`, `deserialize`, `expecting`, `visit_seq`, `test_json`
- **Types:** 3/7 matched (target 6)
- **Missing types:** `Item`, `IntoIter`, `SeqVisitor`, `Value`
- **Tests:** 12/13 matched

### 7. vec_map

- **Target:** `vecmap.VecMap`
- **Similarity:** 0.64
- **Dependents:** 2
- **Priority Score:** 2003303.6
- **Functions:** 32/32 matched (target 36)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 8. ordered_map

- **Target:** `orderedmap.OrderedMap`
- **Similarity:** 0.35
- **Dependents:** 1
- **Priority Score:** 1163806.5
- **Functions:** 20/34 matched (target 44)
- **Missing functions:** `iter_mut`, `values_mut`, `get_mut`, `entry`, `eq`, `hash`, `strong_hash`, `partial_cmp`, `cmp`, `from_iter`, `into_iter`, `serialize`, `deserialize`, `test_serde`
- **Types:** 2/4 matched (target 3)
- **Missing types:** `Item`, `IntoIter`
- **Tests:** 1/2 matched

### 9. unordered_map

- **Target:** `unorderedmap.UnorderedMap`
- **Similarity:** 0.31
- **Dependents:** 1
- **Priority Score:** 1145306.9
- **Functions:** 31/44 matched (target 49)
- **Missing functions:** `get_hashed`, `get_mut`, `entries_unordered_mut`, `values_unordered_mut`, `into_entries_unordered`, `index`, `fmt`, `eq`, `hash`, `from_iter`, `pagable_serialize`, `pagable_deserialize`, `key_mut`
- **Types:** 8/9 matched (target 11)
- **Missing types:** `Output`
- **Tests:** 5/5 matched

### 10. ordered_set

- **Target:** `orderedset.OrderedSet`
- **Similarity:** 0.57
- **Dependents:** 1
- **Priority Score:** 1093604.2
- **Functions:** 24/30 matched (target 39)
- **Missing functions:** `eq`, `partial_cmp`, `cmp`, `hash`, `into_iter`, `from_iter`
- **Types:** 3/6 matched (target 5)
- **Missing types:** `Iter`, `IntoIter`, `Item`
- **Tests:** 2/2 matched

### 11. sorted_vec

- **Target:** `sortedvec.SortedVec`
- **Similarity:** 0.62
- **Dependents:** 1
- **Priority Score:** 1031003.8
- **Functions:** 6/7 matched (target 12)
- **Missing functions:** `test_new_unchecked`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Item`, `IntoIter`
- **Tests:** 0/1 matched

### 12. mix_u32

- **Target:** `starlarkmap.MixU32`
- **Similarity:** 0.27
- **Dependents:** 1
- **Priority Score:** 1000107.2
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

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

- **Target:** `sortedmap.SortedMap`
- **Similarity:** 0.39
- **Dependents:** 0
- **Priority Score:** 92306.1
- **Functions:** 13/20 matched (target 21)
- **Missing functions:** `iter_mut`, `values_mut`, `get_mut`, `from_iter`, `into_iter`, `serialize`, `deserialize`
- **Types:** 1/3 matched (target 2)
- **Missing types:** `Item`, `IntoIter`
- **Tests:** 2/2 matched

### 15. sorted_set

- **Target:** `sortedset.SortedSet`
- **Similarity:** 0.73
- **Dependents:** 0
- **Priority Score:** 41602.7
- **Functions:** 11/13 matched (target 20)
- **Missing functions:** `from_iter`, `into_iter`
- **Types:** 1/3 matched (target 1)
- **Missing types:** `Item`, `IntoIter`

### 16. sorting.insertion

- **Target:** `insertion.Insertion`
- **Similarity:** 0.37
- **Dependents:** 0
- **Priority Score:** 40706.3
- **Functions:** 3/7 matched (target 3)
- **Missing functions:** `test_find_insertion_point`, `find_insertion_point_ints`, `test_insertion_sort`, `insertion_sort_ints`
- **Types:** 0/0 matched
- **Missing types:** _none_
- **Tests:** 0/4 matched

### 17. small_set.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.smallset.iter.Iter`
- **Similarity:** 0.44
- **Dependents:** 0
- **Priority Score:** 30905.6
- **Functions:** 1/3 matched (target 15)
- **Missing functions:** `clone`, `map`
- **Types:** 5/6 matched (target 5)
- **Missing types:** `Item`

### 18. unordered_set

- **Target:** `unorderedset.UnorderedSet`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 22603.5
- **Functions:** 19/21 matched (target 26)
- **Missing functions:** `eq`, `from_iter`
- **Types:** 5/5 matched (target 8)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 19. vec2.iter

- **Target:** `iter.Iter`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 20807.3
- **Functions:** 4/5 matched (target 10)
- **Missing functions:** `drop`
- **Types:** 2/3 matched (target 2)
- **Missing types:** `Item`

### 20. vec_map.simd

- **Target:** `simd.Simd`
- **Similarity:** 0.49
- **Dependents:** 0
- **Priority Score:** 20405.1
- **Functions:** 2/3 matched
- **Missing functions:** `test_find_hash_in_array`
- **Types:** 0/1 matched (target 0)
- **Missing types:** `T`
- **Tests:** 0/1 matched

### 21. vec_map.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.vecmap.iter.Iter`
- **Similarity:** 0.36
- **Dependents:** 0
- **Priority Score:** 11206.4
- **Functions:** 2/2 matched (target 39)
- **Missing functions:** _none_
- **Types:** 9/10 matched (target 9)
- **Missing types:** `Item`

### 22. hash_value

- **Target:** `starlarkmap.HashValue`
- **Similarity:** 0.65
- **Dependents:** 0
- **Priority Score:** 603.5
- **Functions:** 5/5 matched (target 6)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

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
# Re-run the parity audit from the repo root
cd /Volumes/stuff/Projects/kotlinmania/starlarkmap-kotlin
/Volumes/stuff/Projects/kotlinmania/bin/ast_distance --deep tmp/starlark_map/src rust src/commonMain/kotlin/io/github/kotlinmania/starlarkmap kotlin

# Optional: initialize a local task queue for systematic porting
/Volumes/stuff/Projects/kotlinmania/bin/ast_distance --init-tasks tmp/starlark_map/src rust src/commonMain/kotlin/io/github/kotlinmania/starlarkmap kotlin .cache/ast_distance/tasks.json

# Get next high-priority task
/Volumes/stuff/Projects/kotlinmania/bin/ast_distance --assign .cache/ast_distance/tasks.json <agent-id>
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
