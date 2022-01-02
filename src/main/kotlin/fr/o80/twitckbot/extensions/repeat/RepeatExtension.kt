package fr.o80.twitckbot.extensions.repeat

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@SessionScope
class RepeatExtension @Inject constructor(
    private val eventBus: EventBus,
    loggerFactory: LoggerFactory,
) : Extension() {

    private val logger = loggerFactory.getLogger(RepeatExtension::class.java.simpleName)

    private val config: RepeatConfiguration

    init {
        logger.info("Initializing")

        config = readConfig("repeat.json")

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
