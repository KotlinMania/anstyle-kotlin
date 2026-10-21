package ai.solace.tui.anstyle.parse

/** Only allow parsing 7-bit ASCII. */
class AsciiParser : CharAccumulator {
    override fun add(byte: UByte): Int? {
        error("multi-byte UTF8 characters are unsupported")
    }
}
