// port-lint: source macros.rs
package ai.solace.tui.anstyle

/**
 * Creates an ANSI escape sequence: ESC[ + parts + m
 */
internal fun escape(vararg parts: String): String = "\u001B[${parts.joinToString("")}m"

