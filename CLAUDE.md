# Claude Code Project Instructions

## Project Overview

This is **starlarkmap-kotlin**, a line-by-line port of Facebook's `starlark-rust/starlark_map` crate to Kotlin Multiplatform. The Rust sources will live in `tmp/starlark_map/src/` and the Kotlin implementation in `src/commonMain/kotlin/io/github/kotlinmania/starlarkmap/`.

Upstream: <https://github.com/facebook/starlark-rust/tree/main/starlark_map>

## Critical Workflows

### 0. No Subagents

**Do not delegate translation work to subagents (Agent / Task tool).** Translation happens in the main loop where Sydney can see each edit and correct course immediately.

### 1. No Swarm/Task System

This repo is **not** using the swarm/task-assignment workflow (`tasks.json`). Use directory-level `ast_distance --deep` instead, once `tmp/starlark_map` is populated and `tools/ast_distance` is available.

### 2. Port-Lint Headers (REQUIRED)

Every Kotlin file MUST start with:

```kotlin
// port-lint: source <path-relative-to-tmp/starlark_map>
package io.github.kotlinmania.starlarkmap.module
```

Example:

```kotlin
// port-lint: source src/small_map.rs
package io.github.kotlinmania.starlarkmap.smallmap
```

### 3. Namespace

All Kotlin code lives under `io.github.kotlinmania.starlarkmap`, mirroring the Rust crate's module tree:

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
io.github.kotlinmania.starlarkmap.vecmap
```

## Build Commands

```bash
./gradlew build
./gradlew test
./gradlew jvmTest
./gradlew macosArm64Test
```

## STRICT RULES — Translation, Not Engineering

This is a translation project. Every Kotlin file is a line-by-line port of a Rust source file in `tmp/starlark_map/src/`. The `// port-lint: source` header tracks provenance.

- **No code stubs.** Don't write empty placeholder classes or `TODO()` bodies. A missing file is better than a stub.
- **No translator-note comments.** Don't write `// Kotlin: ...` explanations. Don't put Rust syntax inside Kotlin comments — the cheat detector will zero the score.
- **No `// where T: ...` comments retaining the Rust `where` clause.** Translate `where` clauses into Kotlin's `where` syntax.
- **`mod.rs` files are not ported.** Rewire callers to import from the defining module directly.
- **Typealiases follow Rust 1:1.** If Rust declares `pub type Foo = Bar;` as a semantic alias inside a regular `.rs` file, mirror it in Kotlin. If the alias only exists in `mod.rs` as a re-export, drop it and rewire callers.

### Single-file edit, single commit

After every `Edit` / `Write` to a `.kt` file, immediately `git add <file>` and commit before editing anything else. One file → one commit. No bulk regex passes across multiple files.

## Maven Coordinates

```
io.github.kotlinmania:starlarkmap:<version>
```

## Commit Messages

Sydney's style: no AI branding, no Co-Authored-By lines, no emoji. Clear, descriptive messages focused on what changed and why.
