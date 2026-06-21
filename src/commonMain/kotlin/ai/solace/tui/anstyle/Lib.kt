// port-lint: source lib.rs
package ai.solace.tui.anstyle

/*
 * ANSI text styling.
 *
 * A portmanteau of "ANSI style".
 *
 * anstyle provides core types describing ANSI styling escape codes for
 * interoperability between crates.
 *
 * Example use cases:
 * - An argument parser can allow callers to define colors used in help output
 *   without putting a text-formatting crate in the public API.
 * - A style description parser can work with any text-formatting crate.
 *
 * Priorities:
 * 1. API stability
 * 2. Low compile-time and binary-size overhead
 * 3. Const-friendly APIs for callers to statically define their stylesheet
 *
 * Integration crates include anstyle-ansi-term, anstyle-crossterm,
 * anstyle-owo-colors, anstyle-termcolor, and anstyle-yansi.
 *
 * User-styling parsers include anstyle-git and anstyle-ls.
 *
 * Conversion crates include anstream, anstyle-roff, and anstyle-syntect.
 *
 * Utility crates include anstyle-lossy, anstyle-parse, and anstyle-wincon.
 *
 * The core type is Style:
 *
 * val style = Style().bold()
 *
 * Upstream module order:
 * macros
 * color
 * effect
 * reset
 * style
 *
 * Public package surface:
 * color declarations
 * effect declarations
 * reset declarations
 * style declarations
 */

internal const val LIB_DESCRIPTION = "anstyle core types"
