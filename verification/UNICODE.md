# Parser Unicode scalar contract

The pinned Anstyle workspace revision is `14dd743496d1614f8261a093ce755f26d8e2069d`. Its `crates/anstyle-parse/src/lib.rs` defines `CharAccumulator.add` as an optional Unicode scalar and `Perform.print` as one Unicode scalar per event. The workspace lockfile selects `utf8parse 0.2.2`, checksum `06abde3611657adf66d383f00b093d7faecc7fa57071cce2578660c9f1010821`. The existing local Rust crate archive matches this checksum; no replacement compiler or unpublished Kotlin dependency is used.

## Failure and correction

Previously, the bytes `F0 9F 98 80` produced U+FFFD instead of one U+1F600 print event. A Kotlin `Char` is a UTF-16 code unit and cannot hold the required scalar. The decoder also accepted overlong encodings, surrogate code points, and values beyond U+10FFFF.

The actual `CharAccumulator.add` result is now `Int?` and `Perform.print` receives `Int`. The operation and trait names are retained; the payload represents the complete Rust scalar domain. There is no alternate Char callback, split-surrogate dispatch, hidden API, or U+FFFD substitution for valid supplementary characters. This is an intentional source and binary signature change; implementors must migrate and consumers must rebuild per target.

`Utf8Parser` now follows the locked Rust dependency's eight states, eight actions, exact byte ranges, bit placement, and reset behavior. Invalid sequences retain Rust's replacement event and consume the offending byte; the byte is not replayed through another decoder. Incomplete input emits no event. These are the existing parser's decoder implementation, not a claim that an independent KotlinMania utf8parse coordinate has been published.

| Rust declaration | Kotlin declaration |
|---|---|
| Anstyle parser CharAccumulator.add | CharAccumulator.add(UByte): Int? |
| Anstyle parser AsciiParser | AsciiParser |
| Anstyle parser Utf8Parser | Utf8Parser |
| Anstyle parser Perform.print | Perform.print(Int) |
| utf8parse Parser point/state and action dispatch | Utf8Parser private point/state and action dispatch |
| utf8parse private State.advance and Action | Utf8Parser private State.advance and Action |

The real implementation from the upstream module root is split into Parser, CharAccumulator, AsciiParser, Utf8Parser and Perform files; no Lib/Mod reexports or new public result/helper interfaces are introduced.

## Evidence

`ParserUnicodeTest` retains the upstream `advanceUtf8` non-control-text invariant and adds exact scalar boundaries, invalid/recovery sequences, and supplementary text followed by an ANSI escape. All four tests failed against the previous implementation. A real Rust reference executes the checksum-verified dependency for the same11valid and8invalid vectors; source hashes, compiler commands, output and the original failing XML are under workspace `automation-artifacts/2026-10-20-ansi-pr99/anstyle-publication/unicode-repair/`.

The existing test dispatcher now records scalar integers without changing its original escape/CSI/DCS/OSC inputs or assertions. The generated RUST_CALLERS report covers the styling crate; a bounded Kotlin scan of Anstyle, ANSI-to-TUI, Ratatui and env-logger found only the Anstyle test dispatcher implementing the affected parser callback. This is not a claim about every external downstream port.

JVM and Android tests establish their respective JVM/Android behavior only. Native runtime, JS, Wasm and Swift results must be recorded separately. The shared published Swift runtime remains an independent release gate, owned by its existing investigator.

## Function comparison limits

The established workspace `ast_distance --compare-functions` tool was run against the pinned Anstyle parser module root and the locked UTF-8 state source, with complete per-function reports retained alongside the runtime receipts. The parser-file-only comparison matches its seven functions; trait/accumulator methods reside in the other focused files and must not be classified as deleted. The original optional-feature default-constructor/API coverage is not certified by this repair.

The tool labels the unchanged upstream citation URL segment `dec_ansi_parser` as a snake-case comment identifier and forces a zero score for Parser.kt. That URL is a source citation, not Kotlin/Rust syntax. The citation is retained and the tool result is recorded rather than rewritten to improve the score. The private state comparison matches State.advance; byte masks/actions and receiver emission are additionally checked against the real locked Rust reference. A score is not runtime or feature-parity proof.
