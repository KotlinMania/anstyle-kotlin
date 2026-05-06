# anstyle-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fanstyle--kotlin-blue.svg)](https://github.com/KotlinMania/anstyle-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/anstyle-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/anstyle-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/anstyle-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/anstyle-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`rust-cli/anstyle`](https://github.com/rust-cli/anstyle.git).

**Original Project:** This port is based on [`rust-cli/anstyle`](https://github.com/rust-cli/anstyle.git). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `rust-cli/anstyle`

> The text below is reproduced and lightly edited from [`https://github.com/rust-cli/anstyle.git`](https://github.com/rust-cli/anstyle.git). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

This repo contains:
- [`anstream`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstream) for a simple cross platform library for writing colored text to a terminal
- [`anstyle`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle) for style definitions
- User-styling parsers
  - [`anstyle-git`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-git) for parsing `git` style descriptions
  - [`anstyle-ls`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-ls) for parsing `LS_COLORS` style descriptions
- Convert to other formats
  - [`anstyle-roff`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-roff) for converting ANSI codes to `ROFF`
  - [`anstyle-svg`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-svg) for converting ANSI codes to `SVG`
- Styling integration
  - [`anstyle-ansi-term`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-ansi-term) for adapting `anstyle` to `ansi_term`
  - [`anstyle-crossterm`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-crossterm) for adapting `anstyle` to `crossterm`
  - [`anstyle-owo-colors`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-owo-colors) for adapting `anstyle` to `owo-colors`
  - [`anstyle-syntect`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-syntect) for adapting `anstyle` to `syntect`
  - [`anstyle-termcolor`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-termcolor) for adapting `anstyle` to `termcolor`
  - [`anstyle-yansi`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-yansi) for adapting `anstyle` to `yansi`
- Utilities
  - [`anstyle-lossy`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-lossy) for converting between color types
  - [`anstyle-parse`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-parse) for parsing ANSI Style Escapes
  - [`anstyle-wincon`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/anstyle-wincon) for styling legacy Microsoft terminals
  - [`colorchoice-clap`](https://github.com/rust-cli/anstyle/blob/HEAD/crates/colorchoice-clap) for using `color` flag in `clap`

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:anstyle-kotlin:0.1.4")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`rust-cli/anstyle`](https://github.com/rust-cli/anstyle.git). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the anstyle authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`rust-cli/anstyle`](https://github.com/rust-cli/anstyle.git) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
