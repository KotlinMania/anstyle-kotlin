package ai.solace.tui.anstyle.parse

/**
 * Performs actions requested by the [Parser].
 *
 * Actions in this case mean, for example, handling a CSI escape sequence
 * describing cursor movement, or simply printing characters to the screen.
 *
 * The methods on this interface correspond to actions described in
 * <http://vt100.net/emu/dec_ansi_parser>.
 */
interface Perform {
    /**
     * Draw a Unicode scalar value to the screen and update states.
     */
    fun print(c: Int) {}

    /**
     * Execute a C0 or C1 control function.
     */
    fun execute(byte: UByte) {}

    /**
     * Invoked when a final character arrives in first part of device control string.
     *
     * The control function should be determined from the private marker, final character,
     * and execute with a parameter list. A handler should be selected for remaining
     * characters in the string; the handler function should subsequently be called by
     * [put] for every character in the control string.
     *
     * The [ignore] flag indicates that more than two intermediates arrived and
     * subsequent characters were ignored.
     */
    fun hook(params: Params, intermediates: ByteArray, ignore: Boolean, action: UByte) {}

    /**
     * Pass bytes as part of a device control string to the handle chosen in [hook].
     * C0 controls will also be passed to the handler.
     */
    fun put(byte: UByte) {}

    /**
     * Called when a device control string is terminated.
     *
     * The previously selected handler should be notified that the DCS has terminated.
     */
    fun unhook() {}

    /**
     * Dispatch an operating system command.
     */
    fun oscDispatch(params: Array<ByteArray>, bellTerminated: Boolean) {}

    /**
     * A final character has arrived for a CSI sequence.
     *
     * The [ignore] flag indicates that either more than two intermediates arrived
     * or the number of parameters exceeded the maximum supported length,
     * and subsequent characters were ignored.
     */
    fun csiDispatch(params: Params, intermediates: ByteArray, ignore: Boolean, action: UByte) {}

    /**
     * The final character of an escape sequence has arrived.
     *
     * The [ignore] flag indicates that more than two intermediates arrived and
     * subsequent characters were ignored.
     */
    fun escDispatch(intermediates: ByteArray, ignore: Boolean, byte: UByte) {}
}
