package fr.o80.twitckbot.service.connectable.chat

internal class TwitchChatInitChecker(
    private var hasMemberShip: Boolean = false,
    private var hasCommands: Boolean = false,
    private var hasTags: Boolean = false
) {

    val initialized: Boolean
        get() = hasMemberShip && hasCommands && hasTags

    fun handleLine(line: String) {
        if (!initialized) {
            when (line) {
                SERVER_MEMANS -> hasMemberShip = true
                SERVER_CMDANS -> hasCommands = true
                SERVER_TAGANS -> hasTags = true
            }
        }
    }
}
