package fr.o80.twitckbot.extensions.repeat

import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

class RepeatExtension(
    private val eventBus: EventBus
) : Extension() {

    private val config: RepeatConfiguration = readConfig("repeat.json")

    override suspend fun init() {
        val channel: String = config.channel.name
        val intervalBetweenRepeatedMessages: Duration =
            Duration.ofSeconds(config.secondsBetweenRepeatedMessages)
        val messages: List<String> = config.messages

        scope.launch {
            while (true) {
                delay(intervalBetweenRepeatedMessages.toMillis())
                messages.randomOrNull()?.let { message ->
                    eventBus.send(SendMessageEvent(channel, message))
                }
            }
        }
    }
}
