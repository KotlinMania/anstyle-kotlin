package ai.solace.tui.anstyle.parse

/** Build a Unicode scalar value out of bytes. */
interface CharAccumulator {
    /** Return a Unicode scalar value, or null when more data is needed. */
    fun add(byte: UByte): Int?
}
