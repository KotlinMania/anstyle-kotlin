package ai.solace.tui.anstyle.parse

/** Allow parsing UTF-8. */
class Utf8Parser : CharAccumulator {
    private var point: Int = 0
    private var state: State = State.Ground

    override fun add(byte: UByte): Int? {
        val value = byte.toInt()
        val (nextState, action) = state.advance(value)
        val c =
            when (action) {
                Action.InvalidSequence -> {
                    point = 0
                    0xFFFD
                }

                Action.EmitByte -> {
                    value
                }

                Action.SetByte1 -> {
                    val result = point or (value and 0x3F)
                    point = 0
                    result
                }

                Action.SetByte2 -> {
                    point = point or ((value and 0x3F) shl 6)
                    null
                }

                Action.SetByte2Top -> {
                    point = point or ((value and 0x1F) shl 6)
                    null
                }

                Action.SetByte3 -> {
                    point = point or ((value and 0x3F) shl 12)
                    null
                }

                Action.SetByte3Top -> {
                    point = point or ((value and 0x0F) shl 12)
                    null
                }

                Action.SetByte4 -> {
                    point = point or ((value and 0x07) shl 18)
                    null
                }
            }
        state = nextState
        return c
    }

    private enum class Action {
        InvalidSequence,
        EmitByte,
        SetByte1,
        SetByte2,
        SetByte2Top,
        SetByte3,
        SetByte3Top,
        SetByte4,
    }

    private enum class State {
        Ground,
        Tail3,
        Tail2,
        Tail1,
        U32E0,
        U32Ed,
        Utf843F0,
        Utf843F4,
        ;

        fun advance(byte: Int): Pair<State, Action> =
            when (this) {
                Ground -> {
                    when (byte) {
                        in 0x00..0x7F -> Ground to Action.EmitByte
                        in 0xC2..0xDF -> Tail1 to Action.SetByte2Top
                        0xE0 -> U32E0 to Action.SetByte3Top
                        in 0xE1..0xEC -> Tail2 to Action.SetByte3Top
                        0xED -> U32Ed to Action.SetByte3Top
                        in 0xEE..0xEF -> Tail2 to Action.SetByte3Top
                        0xF0 -> Utf843F0 to Action.SetByte4
                        in 0xF1..0xF3 -> Tail3 to Action.SetByte4
                        0xF4 -> Utf843F4 to Action.SetByte4
                        else -> Ground to Action.InvalidSequence
                    }
                }

                U32E0 -> {
                    when (byte) {
                        in 0xA0..0xBF -> Tail1 to Action.SetByte2
                        else -> Ground to Action.InvalidSequence
                    }
                }

                U32Ed -> {
                    when (byte) {
                        in 0x80..0x9F -> Tail1 to Action.SetByte2
                        else -> Ground to Action.InvalidSequence
                    }
                }

                Utf843F0 -> {
                    when (byte) {
                        in 0x90..0xBF -> Tail2 to Action.SetByte3
                        else -> Ground to Action.InvalidSequence
                    }
                }

                Utf843F4 -> {
                    when (byte) {
                        in 0x80..0x8F -> Tail2 to Action.SetByte3
                        else -> Ground to Action.InvalidSequence
                    }
                }

                Tail3 -> {
                    when (byte) {
                        in 0x80..0xBF -> Tail2 to Action.SetByte3
                        else -> Ground to Action.InvalidSequence
                    }
                }

                Tail2 -> {
                    when (byte) {
                        in 0x80..0xBF -> Tail1 to Action.SetByte2
                        else -> Ground to Action.InvalidSequence
                    }
                }

                Tail1 -> {
                    when (byte) {
                        in 0x80..0xBF -> Ground to Action.SetByte1
                        else -> Ground to Action.InvalidSequence
                    }
                }
            }
    }
}
