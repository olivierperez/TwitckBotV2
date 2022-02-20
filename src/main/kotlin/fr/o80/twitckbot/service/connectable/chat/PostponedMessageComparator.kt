package fr.o80.twitckbot.service.connectable.chat

object PostponedMessageComparator : Comparator<PostponedMessage> {

    override fun compare(message1: PostponedMessage, message2: PostponedMessage): Int =
        if (message1.priority.value == message2.priority.value) compareContents(
            message1,
            message2
        )
        else message2.priority.value - message1.priority.value

    private fun compareContents(
        message1: PostponedMessage,
        message2: PostponedMessage
    ): Int {
        return "${message1.channel}+${message1.content}".compareTo("${message2.channel}+${message2.content}")
    }

}
