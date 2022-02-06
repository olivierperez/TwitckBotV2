package fr.o80.twitckbot.extensions.market

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.help.Help
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.RewardEvent
import fr.o80.twitckbot.system.step.StepsExecutor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@SessionScope
class MarketExtension @Inject constructor(
    eventBus: EventBus,
    help: Help,
    loggerFactory: LoggerFactory,
    points: Points,
    stepsExecutor: StepsExecutor
) : Extension() {

    private val logger = loggerFactory.getLogger(MarketExtension::class.java.simpleName)
    private val config: MarketConfiguration

    private val commands: MarketCommands

    private val rewards: MarketRewards

    init {
        logger.info("Initializing")

        config = readConfig("market.json")

        commands = MarketCommands(
            eventBus,
            config.i18n,
            logger,
            points,
            config.products,
            stepsExecutor
        )

        rewards = MarketRewards(
            config.rewards,
            stepsExecutor,
            logger
        )

        with(help) {
            registerCommand("!buy")
            registerCommand("!market")
        }

        scope.launch {
            eventBus.events.filterIsInstance<CommandEvent>().collect { event ->
                commands.interceptCommandEvent(event)
            }
        }
        scope.launch {
            eventBus.events.filterIsInstance<RewardEvent>().collect { event ->
                rewards.interceptRewardEvent(event)
            }
        }
    }
}
