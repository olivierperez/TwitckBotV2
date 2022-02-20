package fr.o80.twitckbot.service.connectable.chat

import java.time.Duration

class PostponedMessage(
    val channel: String,
    val content: String,
    val priority: Priority
)

enum class Priority(val value: Int) {
    IMMEDIATE(Integer.MAX_VALUE), HIGH(1), LOW(0)
}

class CoolDown(
    val duration: Duration
) {
    companion object {
        fun ofSeconds(seconds: Long): CoolDown {
            return CoolDown(Duration.ofSeconds(seconds))
        }
    }
}
