# port-lint Proposed Changes

**Generated:** 2026-05-04
**Source:** tmp/starlark_map/src/hasher.rs
**Target:** src/commonMain/kotlin/io/github/kotlinmania/starlarkmap/Hasher.kt

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/starlarkmap/Hasher.kt` | `// port-lint: source src/hasher.rs` | `// port-lint: source hasher.rs` | `hasher.rs` | `port-lint provenance header matched only after fallback normalization: 'src/hasher.rs' vs expected 'hasher.rs'` |
