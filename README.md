# anstyle-kotlin

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/license-Apache--2.0%2FMIT-blue.svg)](#license)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/anstyle-kotlin?color=blue)](https://central.sonatype.com/artifact/io.github.kotlinmania/anstyle-kotlin)
[![GitHub](https://img.shields.io/badge/github-KotlinMania%2Fanstyle--kotlin-blue?logo=github)](https://github.com/KotlinMania/anstyle-kotlin)

A **Kotlin Multiplatform** library for ANSI text styling and terminal color support. This is a port
of the Rust [anstyle](https://github.com/rust-cli/anstyle) family of crates.

## Overview

anstyle-kotlin provides utilities for rendering styled text to terminals with support for various
color models and terminal capabilities. The library ports multiple related crates from the original
Rust ecosystem:

- **anstyle** - Core ANSI text styling definitions
- **anstream** - IO stream adapters for colored text output
- **anstyle-parse** - ANSI style escape sequence parser
- **anstyle-query** - Terminal capability detection
- **anstyle-roff** - ANSI to ROFF format converter
- **anstyle-svg** - ANSI to SVG format converter
- **anstyle-wincon** - Windows console styling
- **anstyle-lossy** - Color type conversions
- **colorchoice** - Color output control

## Supported Platforms

- macOS (arm64, x64)
- iOS (arm64, x64, simulatorArm64)
- Linux (x64)
- Windows (x64 via MinGW)
- JavaScript (Browser, Node.js)
- WebAssembly (Browser, Node.js)
- Android (API 24+)

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.kotlinmania:anstyle-kotlin:0.1.3")
}
```

### As a Git Submodule

```bash
git submodule add https://github.com/KotlinMania/anstyle-kotlin.git
```

Then in your `settings.gradle.kts`:

```kotlin
include(":anstyle-kotlin")
```

And in your module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":anstyle-kotlin"))
            }
        }
    }
}
```

## Building from Source

```bash
git clone https://github.com/KotlinMania/anstyle-kotlin.git
cd anstyle-kotlin
./gradlew assemble
./gradlew allTests
```

## License

Licensed under either of:

- Apache License, Version 2.0 ([LICENSE-APACHE](./LICENSE-APACHE))
- MIT license ([LICENSE-MIT](./LICENSE-MIT))

at your option.

---

## Acknowledgments

This Kotlin Multiplatform port was created by **Sydney Renee** of [The Solace Project](mailto:sydney@solace.ofharmony.ai)
for [KotlinMania](https://github.com/KotlinMania).

Special thanks to the original authors:

- The [rust-cli](https://github.com/rust-cli) team for the original [anstyle](https://github.com/rust-cli/anstyle) Rust implementation
