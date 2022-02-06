package fr.o80.twitckbot.extensions.channel

import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.BitsEvent
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.FollowsEvent
import fr.o80.twitckbot.system.step.StepsExecutor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChannelExtension @Inject constructor(
    eventBus: EventBus,
    loggerFactory: LoggerFactory,
    stepsExecutor: StepsExecutor
) : Extension() {

    private val logger = loggerFactory.getLogger(ChannelExtension::class.java.simpleName)

    private val config: ChannelConfiguration

    init {
        logger.info("Initializing")

        config = readConfig("channel.json")

        val commands = ChannelCommands(config.commands, stepsExecutor)
        val follows = ChannelFollows(config.followsStep, stepsExecutor)
        val bits = ChannelBits(config.bitsStep, stepsExecutor, logger)

        scope.launch {
            eventBus.events.filterIsInstance<CommandEvent>().collect { event ->
                commands.interceptCommandEvent(event)
            }
        }
        scope.launch {
            eventBus.events.filterIsInstance<FollowsEvent>().collect { event ->
                follows.interceptFollowEvent(event)
            }
        }
        scope.launch {
            eventBus.events.filterIsInstance<BitsEvent>().collect { event ->
                bits.interceptBitsEvent(event)
            }
        }
    }
}
