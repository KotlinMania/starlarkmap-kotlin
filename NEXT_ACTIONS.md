# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 22/25 (88.0%)
- **Function parity:** 483/597 matched (target 741) — 80.9%
- **Class/type parity:** 82/100 matched (target 108) — 82.0%
- **Combined symbol parity:** 565/697 matched (target 849) — 81.1%
- **Average inline-code cosine:** 0.25 (function body across 22 matched files)
- **Average documentation cosine:** 0.61 (doc text across 22 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 21 files with <0.60 function similarity
- **Needs Review:** 1 files with 0.60-0.84 function similarity
- **Excellent:** 0 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

### 1. hashed
- **Similarity:** 0.22 (needs 63% improvement)
- **Dependencies:** 10
- **Priority Score:** 10011808.0
- **Functions:** 16/16 matched (target 21)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`
- **Symbol Deficit:** 1 (functions: 0, types: 1)
- **Action:** Deep review - likely missing major functionality

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. hashed

- **Target:** `starlarkmap.Hashed`
- **Similarity:** 0.22
- **Dependents:** 10
- **Priority Score:** 10011808.0
- **Functions:** 16/16 matched (target 21)
- **Missing functions:** _none_
- **Types:** 1/2 matched (target 1)
- **Missing types:** `Target`

### 2. hasher

- **Target:** `starlarkmap.Hasher`
- **Similarity:** 0.31
- **Dependents:** 6
- **Priority Score:** 6001407.0
- **Functions:** 11/11 matched
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_

### 3. small_map

- **Target:** `smallmap.SmallMap`
- **Similarity:** 0.19
- **Dependents:** 4
- **Priority Score:** 4113108.2
- **Functions:** 110/120 matched (target 157)
- **Missing functions:** `assert_invariants`, `state_check`, `drop`, `from_iter`, `into_iter`, `into_iter`, `into_iter`, `hash`, `partial_cmp`, `cmp`
- **Types:** 11/11 matched (target 17)
- **Missing types:** _none_
- **Tests:** 24/29 matched

### 4. vec2

- **Target:** `vec2.Vec2`
- **Similarity:** 0.08
- **Dependents:** 3
- **Priority Score:** 3467809.2
- **Functions:** 31/68 matched (target 39)
- **Missing functions:** `new_checked`, `alloc`, `dealloc`, `pagable_serialize`, `pagable_deserialize`, `serialize`, `deserialize`, `expecting`, `visit_seq`, `default`, `fmt`, `clone`, `new`, `aaa_ptr`, `bbb_ptr`, `aaa`, `aaa_mut`, `aaa_uninit`, `bbb`, `bbb_mut`, `bbb_uninit`, `reserve_slow`, `dealloc_impl`, `dealloc`, `drop_in_place`, `get_mut`, `get_unchecked_mut`, `read`, `drop`, `drop`, `drop`, `into_iter`, `eq`, `hash`, `test_layout_for`, `test_alloc_dealloc`, `test_alignment`
- **Types:** 1/10 matched (target 2)
- **Missing types:** `Vec2Layout`, `Vec2Visitor`, `Value`, `DropInPlace`, `Retain`, `Item`, `IntoIter`, `Align16`, `Align8`
- **Tests:** 9/12 matched

### 5. small_set

- **Target:** `smallset.SmallSet`
- **Similarity:** 0.17
- **Dependents:** 2
- **Priority Score:** 2037708.2
- **Functions:** 68/70 matched (target 91)
- **Missing functions:** `into_iter`, `size_hint`
- **Types:** 6/7 matched (target 9)
- **Missing types:** `Value`
- **Tests:** 15/15 matched

### 6. vec_map

- **Target:** `vecmap.VecMap`
- **Similarity:** 0.19
- **Dependents:** 2
- **Priority Score:** 2003308.1
- **Functions:** 32/32 matched (target 37)
- **Missing functions:** _none_
- **Types:** 1/1 matched
- **Missing types:** _none_

### 7. unordered_map

- **Target:** `unorderedmap.UnorderedMap`
- **Similarity:** 0.09
- **Dependents:** 1
- **Priority Score:** 1176309.0
- **Functions:** 38/54 matched (target 50)
- **Missing functions:** `get_hashed`, `get_mut`, `entries_unordered_mut`, `values_unordered_mut`, `into_entries_unordered`, `index`, `fmt`, `eq`, `hash`, `from_iter`, `pagable_serialize`, `pagable_deserialize`, `get_mut`, `get_mut`, `key_mut`, `hash`
- **Types:** 8/9 matched (target 11)
- **Missing types:** `Output`
- **Tests:** 5/6 matched

### 8. ordered_set

- **Target:** `orderedset.OrderedSet`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1083907.9
- **Functions:** 26/33 matched (target 47)
- **Missing functions:** `eq`, `partial_cmp`, `cmp`, `hash`, `into_iter`, `eq`, `hash`
- **Types:** 5/6 matched (target 7)
- **Missing types:** `Iter`
- **Tests:** 2/4 matched

### 9. ordered_map

- **Target:** `orderedmap.OrderedMap`
- **Similarity:** 0.21
- **Dependents:** 1
- **Priority Score:** 1044207.9
- **Functions:** 34/38 matched (target 64)
- **Missing functions:** `into_iter`, `into_iter`, `eq`, `hash`
- **Types:** 4/4 matched (target 5)
- **Missing types:** _none_
- **Tests:** 2/4 matched

### 10. sorted_vec

- **Target:** `sortedvec.SortedVec`
- **Similarity:** 0.17
- **Dependents:** 1
- **Priority Score:** 1001008.3
- **Functions:** 7/7 matched (target 14)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 11. mix_u32

- **Target:** `starlarkmap.MixU32`
- **Similarity:** 0.51
- **Dependents:** 1
- **Priority Score:** 1000104.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched
- **Missing types:** _none_

### 12. small_map.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.smallmap.iter.Iter`
- **Similarity:** 0.08
- **Dependents:** 0
- **Priority Score:** 254409.2
- **Functions:** 8/32 matched (target 30)
- **Missing functions:** `map`, `map`, `map`, `map`, `map`, `map`, `map`, `map`, `map`, `len`, `map`, `len`, `map`, `len`, `_assert_iterators_sync_send`, `assert_sync_send`, `test_iter_hashed`, `test_iter`, `test_into_iter_hashed`, `test_into_iter`, `test_keys`, `test_values`, `test_into_keys`, `test_into_values`
- **Types:** 11/12 matched (target 11)
- **Missing types:** `Item`

### 13. small_set.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.smallset.iter.Iter`
- **Similarity:** 0.14
- **Dependents:** 0
- **Priority Score:** 71708.6
- **Functions:** 5/11 matched (target 15)
- **Missing functions:** `clone`, `map`, `map`, `map`, `map`, `map`
- **Types:** 5/6 matched (target 5)
- **Missing types:** `Item`

### 14. unordered_set

- **Target:** `unorderedset.UnorderedSet`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 22807.3
- **Functions:** 21/23 matched (target 27)
- **Missing functions:** `eq`, `from_iter`
- **Types:** 5/5 matched (target 8)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 15. sorted_map

- **Target:** `sortedmap.SortedMap`
- **Similarity:** 0.29
- **Dependents:** 0
- **Priority Score:** 22607.1
- **Functions:** 21/23 matched (target 36)
- **Missing functions:** `into_iter`, `into_iter`
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 16. vec2.iter

- **Target:** `iter.Iter`
- **Similarity:** 0.05
- **Dependents:** 0
- **Priority Score:** 21209.5
- **Functions:** 8/9 matched (target 10)
- **Missing functions:** `drop`
- **Types:** 2/3 matched (target 2)
- **Missing types:** `Item`

### 17. sorting.insertion

- **Target:** `insertion.Insertion`
- **Similarity:** 0.43
- **Dependents:** 0
- **Priority Score:** 20705.7
- **Functions:** 5/7 matched
- **Missing functions:** `find_insertion_point_ints`, `insertion_sort_ints`
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_
- **Tests:** 2/4 matched

### 18. vec_map.iter

- **Target:** `commonMain.kotlin.io.github.kotlinmania.starlarkmap.vecmap.iter.Iter`
- **Similarity:** 0.07
- **Dependents:** 0
- **Priority Score:** 12709.3
- **Functions:** 17/17 matched (target 39)
- **Missing functions:** _none_
- **Types:** 9/10 matched (target 9)
- **Missing types:** `Item`

### 19. sorted_set

- **Target:** `sortedset.SortedSet`
- **Similarity:** 0.38
- **Dependents:** 0
- **Priority Score:** 11906.2
- **Functions:** 15/16 matched (target 33)
- **Missing functions:** `into_iter`
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_

### 20. vec_map.simd

- **Target:** `simd.Simd`
- **Similarity:** 0.34
- **Dependents:** 0
- **Priority Score:** 10406.6
- **Functions:** 3/3 matched (target 4)
- **Missing functions:** _none_
- **Types:** 0/1 matched
- **Missing types:** `T`
- **Tests:** 1/1 matched

### 21. hash_value

- **Target:** `starlarkmap.HashValue`
- **Similarity:** 0.36
- **Dependents:** 0
- **Priority Score:** 606.4
- **Functions:** 5/5 matched (target 7)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 22. vec_map.hint

- **Target:** `hint.Hint`
- **Similarity:** 0.72
- **Dependents:** 0
- **Priority Score:** 102.8
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

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `iter` | `Iter` | 4 | `iter.rs` | `Iter.kt` |
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |
| `sorting` | `sorting.Sorting` | 0 | `sorting.rs` | `sorting/Sorting.kt` |

