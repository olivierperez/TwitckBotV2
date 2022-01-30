package fr.o80.twitckbot.extensions.points

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@SessionScope
class PointsExtension @Inject constructor(
    private val eventBus: EventBus,
    // TODO Help
    // private val help: HelpExtension?,
    private val bank: PointsBank,
    loggerFactory: LoggerFactory,
    storage: Storage,
) : Extension(), Points {

    private val logger = loggerFactory.getLogger(PointsExtension::class.java.simpleName)

    private val config: PointsConfiguration

    private val pointsCommands: PointsCommands

    init {
        logger.info("Initializing")

        config = readConfig("points.json")

        pointsCommands = PointsCommands(
            config.channel.name,
            config.privilegedBadges,
            config.i18n,
            bank,
            logger,
            storage,
            eventBus
        )

        scope.launch {
            eventBus.events.filterIsInstance<CommandEvent>().collect { event ->
                pointsCommands.interceptCommandEvent(event)
            }
        }

        /*help?.run {
            registerCommand(POINTS_COMMAND)
            registerCommand(POINTS_GIVE_COMMAND)
        }*/
    }

    override fun getPoints(login: String): Int {
        return bank.getPoints(login)
    }

    override fun addPoints(login: String, points: Int) {
        bank.addPoints(login, points)
    }

    override fun consumePoints(login: String, points: Int): Boolean {
        return bank.removePoints(login, points)
    }
}