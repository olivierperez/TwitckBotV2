package fr.o80.twitckbot.service.connectable.chat

interface IrcMessenger {

    fun sendLine(message: String)

    fun send(
        channel: String,
        message: String
    )
}
