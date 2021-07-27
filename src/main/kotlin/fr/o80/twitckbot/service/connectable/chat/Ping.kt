package fr.o80.twitckbot.service.connectable.chat

internal class Ping(private val messenger: IrcMessenger) {
    fun handleLine(line: String) {
        if (line == "PING :tmi.twitch.tv") {
            messenger.sendLine("PONG :tmi.twitch.tv")
        }
    }
}
