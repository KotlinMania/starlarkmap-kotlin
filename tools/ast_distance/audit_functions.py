#!/usr/bin/env python
"""Per-file function inventory for transliteration audits.

For each .rs file under <rust_root>, list its function names. For each .kt
file under <kotlin_root>, read the `// port-lint: source <path>` header,
match it to the .rs file it claims, list the .kt's function names. Then
report:

  WRONG-FILE — a function name appears in this .kt slot but is *not* defined
               in the matched .rs and *is* defined in some other .rs file.
               This is the classic copy-paste-into-the-wrong-port drift case.

  UNPORTED   — function names defined in the .rs but missing from every .kt
               that targets that .rs.

  KT-ONLY    — function names defined in a .kt slot with no matching .rs fn
               anywhere in the source tree. Often Kotlin idiom (fromIterator,
               hasNext, equivalent), sometimes drift.

Names are normalized so the comparison ignores stylistic difference:

  * Rust snake_case is camelCased.
  * Common Kotlin overrides are mapped to their Rust analog:
        hashCode -> hash
        equals   -> eq
        toString -> fmt
        iterator -> intoIter

Wrong-file output filters per-type derive noise: a function appearing in
≥3 .rs files is treated as a derived/per-type override (`fmt`, `eq`,
`hash`, `default`, `clone`, `drop`, …) rather than drift. Tune the
`DERIVE_NOISE_THRESHOLD` constant if you want a louder or quieter report.

Usage:

    cd <project-root>
    python tools/ast_distance/audit_functions.py \
        tmp/<crate>/src \
        src/commonMain/kotlin/io/github/kotlinmania/<crate>

The two paths point at the Rust root (under tmp/) and the Kotlin package
root (under src/commonMain/kotlin/...). The script walks both trees.
"""

import os
import re
import sys
from collections import defaultdict


RUST_FN = re.compile(
    r'^\s*(?:pub(?:\s*\([^)]*\))?\s+)?'
    r'(?:async\s+|const\s+|unsafe\s+|extern(?:\s*"[^"]*")?\s+)*'
    r'fn\s+([A-Za-z_][A-Za-z0-9_]*)\s*[(<]'
)
KOTLIN_FN = re.compile(
    r'^\s*(?:(?:private|internal|public|protected|override|open|abstract|'
    r'final|inline|noinline|crossinline|operator|infix|tailrec|suspend|'
    r'external|expect|actual|companion|object)\s+)*'
    r'fun(?:\s*<[^>]+>)?\s+(?:[A-Za-z_][A-Za-z0-9_.]*\s*\.)?'
    r'([A-Za-z_][A-Za-z0-9_]*)\s*[(<]'
)
PORT_LINT = re.compile(r'^//\s*port-lint:\s*source\s+(.+?)\s*$')

KT_TO_RS = {
    'hashCode': 'hash',
    'equals': 'eq',
    'toString': 'fmt',
    'iterator': 'intoIter',
}

DERIVE_NOISE_THRESHOLD = 3


def snake_to_camel(name):
    parts = name.split('_')
    return parts[0] + ''.join(p[:1].upper() + p[1:] for p in parts[1:])


def normalize(name, lang):
    if lang == 'rust':
        return snake_to_camel(name)
    return KT_TO_RS.get(name, name)


def rust_fns(path):
    names = set()
    with open(path, 'r', errors='replace') as f:
        for line in f:
            m = RUST_FN.search(line)
            if m:
                names.add(normalize(m.group(1), 'rust'))
    return names


def kotlin_fns(path):
    header = None
    names = set()
    with open(path, 'r', errors='replace') as f:
        for line in f:
            if header is None:
                hm = PORT_LINT.match(line)
                if hm:
                    header = hm.group(1).strip()
            m = KOTLIN_FN.match(line)
            if m:
                names.add(normalize(m.group(1), 'kotlin'))
    return header, names


def normalize_header(header):
    if not header:
        return None
    if 'external crate' in header or 'ignore' in header:
        return None
    h = header
    if h.startswith('tests:'):
        h = h[len('tests:'):]
    return h.strip()


def header_match_key(path):
    """Lower-cased path with src/ stripped and underscores removed.
    Recovers a match across the drift forms documented in CLAUDE.md
    (camelCase versus snake_case, missing src/ prefix, etc.)."""
    s = path.lower().replace('_', '')
    if s.startswith('src/'):
        s = s[4:]
    return s


def main(rust_root, kotlin_root):
    rs_files = []
    for dp, _, fs in os.walk(rust_root):
        for fn in fs:
            if fn.endswith('.rs'):
                rs_files.append(os.path.join(dp, fn))

    kt_files = []
    for dp, _, fs in os.walk(kotlin_root):
        for fn in fs:
            if fn.endswith('.kt'):
                kt_files.append(os.path.join(dp, fn))

    rs_inventory = {}
    rs_fn_to_files = defaultdict(set)
    for p in rs_files:
        rel = os.path.relpath(p, rust_root)
        names = rust_fns(p)
        rs_inventory[rel] = names
        for n in names:
            rs_fn_to_files[n].add(rel)

    rs_keymap = {header_match_key(rel): rel for rel in rs_inventory}

    kt_by_target = defaultdict(list)
    misheaders = []
    for p in kt_files:
        header, names = kotlin_fns(p)
        h = normalize_header(header)
        if h is None:
            continue
        match = rs_keymap.get(header_match_key(h))
        if match is None:
            misheaders.append((p, header))
            continue
        kt_by_target[match].append((p, names))

    print("=" * 80)
    print("PER-FILE FUNCTION AUDIT (normalized: snake->camel; KT overrides->RS)")
    print("=" * 80)
    print(f"Rust:   {rust_root} ({len(rs_files)} files)")
    print(f"Kotlin: {kotlin_root} ({len(kt_files)} files)")
    print()

    if misheaders:
        print("--- HEADERS THAT DO NOT RESOLVE TO ANY .rs FILE ---")
        for p, h in misheaders:
            print(f"  {os.path.relpath(p, kotlin_root)}  <-  {h!r}")
        print()

    print("--- WRONG-FILE FUNCTIONS "
          "(KT defines fn here; RS fn of same name is elsewhere) ---")
    print(f"    (per-type derives — fns appearing in "
          f"≥{DERIVE_NOISE_THRESHOLD} .rs files — are filtered as noise)")
    any_wrong = False
    for rel in sorted(rs_inventory):
        rs_names = rs_inventory[rel]
        kt_pairs = kt_by_target.get(rel, [])
        kt_all = set()
        for _, n in kt_pairs:
            kt_all |= n
        only_kt = kt_all - rs_names
        wrong_in_this_file = []
        for n in sorted(only_kt):
            other_files = rs_fn_to_files.get(n, set())
            if not other_files:
                continue
            if rel in other_files:
                continue
            if len(other_files) >= DERIVE_NOISE_THRESHOLD:
                continue
            wrong_in_this_file.append((n, sorted(other_files)))
        if wrong_in_this_file:
            any_wrong = True
            kts = ", ".join(os.path.relpath(p, kotlin_root)
                            for p, _ in kt_pairs)
            print(f"\n  {rel}  ->  {kts}")
            for n, hits in wrong_in_this_file:
                print(f"    {n}() defined upstream only in: "
                      f"{', '.join(hits)}")
    if not any_wrong:
        print("  (none after filtering)")
    print()

    print("--- UNPORTED FUNCTIONS (per Rust file) ---")
    for rel in sorted(rs_inventory):
        rs_names = rs_inventory[rel]
        kt_pairs = kt_by_target.get(rel, [])
        kt_all = set()
        for _, n in kt_pairs:
            kt_all |= n
        unported = sorted(rs_names - kt_all)
        if not unported:
            continue
        print(f"\n  {rel}")
        if not kt_pairs:
            print(f"    (no Kotlin file claims this Rust file)")
        print(f"    missing: {', '.join(unported)}")
    print()

    print("--- KT-ONLY FUNCTIONS "
          "(no matching Rust fn anywhere — review for drift) ---")
    any_extra = False
    for rel in sorted(rs_inventory):
        rs_names = rs_inventory[rel]
        kt_pairs = kt_by_target.get(rel, [])
        kt_all = set()
        for _, n in kt_pairs:
            kt_all |= n
        only_kt = kt_all - rs_names
        true_extras = sorted(n for n in only_kt
                             if not rs_fn_to_files.get(n))
        if true_extras:
            any_extra = True
            kts = ", ".join(os.path.relpath(p, kotlin_root)
                            for p, _ in kt_pairs)
            print(f"\n  {rel}  ->  {kts}")
            print(f"    kt-only: {', '.join(true_extras)}")
    if not any_extra:
        print("  (none)")


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("usage: audit_functions.py <rust_root> <kotlin_root>",
              file=sys.stderr)
        sys.exit(1)
    main(sys.argv[1], sys.argv[2])
