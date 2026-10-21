package ai.solace.tui.anstyle.parse

import ai.solace.tui.anstyle.parse.state.Action
import ai.solace.tui.anstyle.parse.state.State
import ai.solace.tui.anstyle.parse.state.stateChange

/**
 * Parser for implementing virtual terminal emulators.
 *
 * Parser is implemented according to Paul Williams' ANSI parser state machine.
 * The state machine doesn't assign meaning to the parsed data and is thus not
 * itself sufficient for writing a terminal emulator. Instead, it is expected
 * that an implementation of [Perform] is provided which does something useful
 * with the parsed data. The Parser handles the book keeping, and the Perform
 * gets to simply handle actions.
 *
 * @see <a href="https://vt100.net/emu/dec_ansi_parser">Paul Williams' ANSI parser state machine</a>
 */
class Parser<C : CharAccumulator>(
    private val utf8Parser: C,
) {
    private var state: State = State.Ground
    private val intermediates: ByteArray = ByteArray(MAX_INTERMEDIATES)
    private var intermediateIdx: Int = 0
    private val params: Params = Params()
    private var param: UShort = 0u
    private val oscRaw: MutableList<UByte> = mutableListOf()
    private val oscParams: Array<Pair<Int, Int>> = Array(MAX_OSC_PARAMS) { 0 to 0 }
    private var oscNumParams: Int = 0
    private var ignoring: Boolean = false

    /**
     * Get the current parameters.
     */
    fun params(): Params = params

    /**
     * Get the current intermediates.
     */
    fun intermediates(): ByteArray = intermediates.copyOfRange(0, intermediateIdx)

    /**
     * Advance the parser state.
     *
     * Requires a [Perform] in case [byte] triggers an action.
     */
    fun <P : Perform> advance(performer: P, byte: UByte) {
        // UTF-8 characters are handled out-of-band.
        if (state == State.Utf8) {
            processUtf8(performer, byte)
            return
        }

        val (nextState, action) = stateChange(state, byte)
        performStateChange(performer, nextState, action, byte)
    }

    private fun <P : Perform> processUtf8(performer: P, byte: UByte) {
        val c = utf8Parser.add(byte)
        if (c != null) {
            performer.print(c)
            state = State.Ground
        }
    }

    private fun <P : Perform> performStateChange(performer: P, nextState: State, action: Action, byte: UByte) {
        when (nextState) {
            State.Anywhere -> {
                // Just run the action
                performAction(performer, action, byte)
            }

            else -> {
                // Exit actions
                when (state) {
                    State.DcsPassthrough -> {
                        performAction(performer, Action.Unhook, byte)
                    }

                    State.OscString -> {
                        performAction(performer, Action.OscEnd, byte)
                    }

                    else -> {}
                }

                // Transition action
                if (action != Action.Nop) {
                    performAction(performer, action, byte)
                }

                // Entry actions
                when (nextState) {
                    State.CsiEntry, State.DcsEntry, State.Escape -> {
                        performAction(performer, Action.Clear, byte)
                    }

                    State.DcsPassthrough -> {
                        performAction(performer, Action.Hook, byte)
                    }

                    State.OscString -> {
                        performAction(performer, Action.OscStart, byte)
                    }

                    else -> {}
                }

                // Assume the new state
                state = nextState
            }
        }
    }

    /**
     * Dispatch the collected operating system command parameters.
     *
     * The aliasing is needed here for multiple slices into oscRaw.
     */
    private fun <P : Perform> oscDispatch(performer: P, byte: UByte) {
        val slices =
            Array(oscNumParams) { i ->
                val (start, end) = oscParams[i]
                oscRaw.subList(start, end).map { it.toByte() }.toByteArray()
            }
        performer.oscDispatch(slices, byte == 0x07.toUByte())
    }

    private fun <P : Perform> performAction(performer: P, action: Action, byte: UByte) {
        when (action) {
            Action.Print -> {
                performer.print(byte.toInt())
            }

            Action.Execute -> {
                performer.execute(byte)
            }

            Action.Hook -> {
                if (params.isFull()) {
                    ignoring = true
                } else {
                    params.push(param)
                }
                performer.hook(params(), intermediates(), ignoring, byte)
            }

            Action.Put -> {
                performer.put(byte)
            }

            Action.OscStart -> {
                oscRaw.clear()
                oscNumParams = 0
            }

            Action.OscPut -> {
                val idx = oscRaw.size

                // Param separator
                if (byte == ';'.code.toUByte()) {
                    val paramIdx = oscNumParams
                    when {
                        // Only process up to MAX_OSC_PARAMS
                        paramIdx == MAX_OSC_PARAMS -> {
                            return
                        }

                        // First param is special - 0 to current byte index
                        paramIdx == 0 -> {
                            oscParams[paramIdx] = 0 to idx
                        }

                        // All other params depend on previous indexing
                        else -> {
                            val (_, prevEnd) = oscParams[paramIdx - 1]
                            oscParams[paramIdx] = prevEnd to idx
                        }
                    }
                    oscNumParams += 1
                } else {
                    oscRaw.add(byte)
                }
            }

            Action.OscEnd -> {
                val paramIdx = oscNumParams
                val idx = oscRaw.size

                when {
                    // Finish last parameter if not already maxed
                    paramIdx == MAX_OSC_PARAMS -> {}

                    // First param is special - 0 to current byte index
                    paramIdx == 0 -> {
                        oscParams[paramIdx] = 0 to idx
                        oscNumParams += 1
                    }

                    // All other params depend on previous indexing
                    else -> {
                        val (_, prevEnd) = oscParams[paramIdx - 1]
                        oscParams[paramIdx] = prevEnd to idx
                        oscNumParams += 1
                    }
                }
                oscDispatch(performer, byte)
            }

            Action.Unhook -> {
                performer.unhook()
            }

            Action.CsiDispatch -> {
                if (params.isFull()) {
                    ignoring = true
                } else {
                    params.push(param)
                }
                performer.csiDispatch(params(), intermediates(), ignoring, byte)
            }

            Action.EscDispatch -> {
                performer.escDispatch(intermediates(), ignoring, byte)
            }

            Action.Collect -> {
                if (intermediateIdx == MAX_INTERMEDIATES) {
                    ignoring = true
                } else {
                    intermediates[intermediateIdx] = byte.toByte()
                    intermediateIdx += 1
                }
            }

            Action.Param -> {
                if (params.isFull()) {
                    ignoring = true
                    return
                }

                when {
                    byte == ';'.code.toUByte() -> {
                        params.push(param)
                        param = 0u
                    }

                    byte == ':'.code.toUByte() -> {
                        params.extend(param)
                        param = 0u
                    }

                    else -> {
                        // Continue collecting bytes into param
                        val digit = (byte - '0'.code.toUByte()).toUShort()
                        param = minOf((param.toUInt() * 10u), UShort.MAX_VALUE.toUInt()).toUShort()
                        param = minOf((param.toUInt() + digit.toUInt()), UShort.MAX_VALUE.toUInt()).toUShort()
                    }
                }
            }

            Action.Clear -> {
                // Reset everything on ESC/CSI/DCS entry
                intermediateIdx = 0
                ignoring = false
                param = 0u
                params.clear()
            }

            Action.BeginUtf8 -> {
                processUtf8(performer, byte)
            }

            Action.Ignore -> {}

            Action.Nop -> {}
        }
    }

    companion object {
        private const val MAX_INTERMEDIATES: Int = 2
        private const val MAX_OSC_PARAMS: Int = 16
    }
}
