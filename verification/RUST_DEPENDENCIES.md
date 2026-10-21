# Pinned Rust dependency mapping

Upstream `rust-cli/anstyle` revision `14dd743496d1614f8261a093ce755f26d8e2069d` is recorded by the existing packaged Anstyle `.cargo_vcs_info.json`. The full repository Git tree contains **19 manifests**: the root workspace and 18 member crates. All 19 original manifest bytes and SHA-256 hashes are retained in workspace artifacts at `automation-artifacts/2026-10-20-ansi-pr99/anstyle-publication/upstream-manifests/` and `upstream-workspace-manifests.json`. `upstream-dependency-edges.json` records every normal/dev/build/target edge, optional flag, feature list, and default-feature setting.

The root workspace uses `crates/*`. Package repository/license/edition/Rust-version/include and lint settings are inherited; there is no root workspace dependency table to substitute guessed versions. Member dependency versions and features therefore come directly from each declaration. Path edges remain explicit member edges, not external coordinates.

| Manifest | Crate | Version | Kotlin ownership / coverage |
|---|---|---|---|
| `crates/anstream/Cargo.toml` | `anstream` | `0.6.20` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-ansi-term/Cargo.toml` | `anstyle-ansi-term` | `1.0.4` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-crossterm/Cargo.toml` | `anstyle-crossterm` | `4.0.2` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-git/Cargo.toml` | `anstyle-git` | `1.1.3` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-lossy/Cargo.toml` | `anstyle-lossy` | `1.1.4` | Existing lossy package; shares the styling owner in this publication. |
| `crates/anstyle-ls/Cargo.toml` | `anstyle-ls` | `1.0.5` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-owo-colors/Cargo.toml` | `anstyle-owo-colors` | `2.0.4` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-parse/Cargo.toml` | `anstyle-parse` | `0.2.7` | Existing parse package; UTF-8 scalar fidelity is not established. |
| `crates/anstyle-query/Cargo.toml` | `anstyle-query` | `1.1.4` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-roff/Cargo.toml` | `anstyle-roff` | `0.3.8` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-svg/Cargo.toml` | `anstyle-svg` | `0.1.11` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-syntect/Cargo.toml` | `anstyle-syntect` | `1.0.4` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-termcolor/Cargo.toml` | `anstyle-termcolor` | `1.1.4` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-wincon/Cargo.toml` | `anstyle-wincon` | `3.0.10` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle-yansi/Cargo.toml` | `anstyle-yansi` | `2.0.3` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/anstyle/Cargo.toml` | `anstyle` | `1.0.13` | Root styling API: Color, Style, Effects, Reset; published root coordinate. |
| `crates/colorchoice-clap/Cargo.toml` | `colorchoice-clap` | `1.0.7` | Separate upstream library with no source package in this artifact; not claimed as implemented by the root styling coordinate. |
| `crates/colorchoice/Cargo.toml` | `colorchoice` | `1.0.4` | Existing colorchoice package; uses Kotlin atomic state. |

## Styling coordinate

`crates/anstyle` has **zero normal dependencies**, default feature `std`, empty `std` feature, and dev-only `lexopt`/`snapbox`. Current Kotlin production imports reference only Kotlin and same-artifact packages; the previous template coroutines, serialization, datetime, and immutable-collections bundle had no source consumers. Its runtime edge and serialization compiler plugin were removed rather than excluded from resolution. Benchmark/compiler tooling remains separate from production dependencies.

`From` color conversions map to `Color.Companion.from` and `RgbColor.Companion.from`; `Style::new` and conversion from `Effects` map to `Style.Companion.new` and `Style.Companion.from`. Kotlin companion receivers are necessary for downstream Rust-derived conversion implementations. Source companions are not evidence that Central 0.1.5 contains them: actual 0.1.5 JVM bytecode has companions only for `Ansi256Color` and `Effects`.

## Additional bundled members and verification limits

The existing artifact also contains parse/lossy/colorchoice packages. `anstyle-lossy` has a normal path edge to Anstyle. `colorchoice` has no normal dependencies. `anstyle-parse` defaults to feature `utf8`, which enables `utf8parse`; optional feature `core` enables `arrayvec` with default features disabled. Those declarations do not justify an unrelated kotlinx runtime bundle.

The current local parser implements a separate `Utf8Parser` returning a Kotlin `Char`, replacing supplementary scalar values with U+FFFD. This does not establish Rust `char`/utf8parse parity; the public `Perform.print(Char)` boundary cannot represent every Rust scalar. Parser feature/dependency parity is incomplete. No speculative dependency exclusion, source substitution, or replacement assertion establishes otherwise.

The existing color `noAlign` test calls the same formatting operation twice, without the upstream requested width. `printSizeOf` prints descriptions rather than actual memory sizes. Their successful execution is not proof of those Rust invariants. Swift tests now exercise actual typed colors, rendering, effects and immutable style behavior, but the strict compiler-runtime failure prevents execution in the current gate.

All-target release acceptance additionally requires actual realized compilation/source ancestry, platform execution and immutable per-variant publication/consumer evidence. The earlier green workflow used warning suppression and generated API deletion, so it does not certify those conditions.

The realized graph receipt follows both source-set ancestry and KGP associated compilations. Auxiliary example compilations may obtain common main code through a main-compilation association; absence from their direct source set list alone is not a hierarchy defect. The verifier records direct sets and associations explicitly.
