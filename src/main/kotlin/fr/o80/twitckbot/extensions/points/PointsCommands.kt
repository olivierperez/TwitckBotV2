package fr.o80.twitckbot.extensions.points

import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.system.bean.Badge
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import fr.o80.twitckbot.utils.sanitizeLogin
import fr.o80.twitckbot.utils.tryToInt

const val POINTS_ADD_COMMAND = "!points_add"
const val POINTS_GIVE_COMMAND = "!points_give"
const val POINTS_COMMAND = "!points"
const val POINTS_COMMAND_2 = "!points_info"

class PointsCommands(
    private val channel: String,
    private val privilegedBadges: Collection<Badge>,
    private val message: PointsI18n,
    private val bank: PointsBank,
    private val logger: Logger,
    private val storage: Storage,
    private val eventBus: EventBus
) {

    suspend fun interceptCommandEvent(commandEvent: CommandEvent): CommandEvent {
        if (channel != commandEvent.channel)
            return commandEvent

        when (commandEvent.command.tag) {
            // !points_add Pipiks_ 13000
            POINTS_ADD_COMMAND -> handleAddCommand(commandEvent)
            // !points_give idontwantgiftsub 152
            POINTS_GIVE_COMMAND -> handleGiveCommand(commandEvent)
            // !points
            POINTS_COMMAND, POINTS_COMMAND_2 -> handleInfoCommand(commandEvent)
        }

        return commandEvent
    }

    private suspend fun handleAddCommand(commandEvent: CommandEvent) {
        if (commandEvent.viewer hasNoPrivilegesOf privilegedBadges) return

        val command = commandEvent.command
        if (command.options.size == 2) {
            val login = command.options[0].sanitizeLogin()
            val points = command.options[1].tryToInt()
            logger.command(
                command,
                "${commandEvent.viewer.displayName} try to add $points to $login"
            )

            points?.let {
                bank.addPoints(login, points)
            }
        }
    }

    private suspend fun handleGiveCommand(commandEvent: CommandEvent) {
        val command = commandEvent.command
        if (command.options.size == 2) {
            val fromLogin = commandEvent.viewer.login
            val toLogin = command.options[0].sanitizeLogin()
            val points = command.options[1].tryToInt()
            logger.command(
                command,
                "${commandEvent.viewer.displayName} try to transfer $points to $toLogin"
            )

            if (toLogin == fromLogin) return
            if (points == null) return
            if (!storage.hasUserInfo(toLogin)) {
                eventBus.send(
                    SendMessageEvent(
                        commandEvent.channel,
                        message.destinationViewerDoesNotExist
                    )
                )
                return
            }

            val transferSucceeded = bank.transferPoints(fromLogin, toLogin, points)
            val msg = if (transferSucceeded) {
                message.pointsTransferred
                    .replace("#FROM#", commandEvent.viewer.displayName)
                    .replace("#TO#", toLogin)
            } else {
                message.notEnoughPoints
                    .replace("#FROM#", commandEvent.viewer.displayName)
                    .replace("#TO#", toLogin)
            }

            // TODO Send whispering
            // messenger.whisper(commandEvent.channel, commandEvent.viewer.login, msg)
        }
    }

    private suspend fun handleInfoCommand(commandEvent: CommandEvent) {
        val command = commandEvent.command
        val points = bank.getPoints(commandEvent.viewer.login)

        logger.command(
            command,
            "${commandEvent.viewer.displayName} requested points info ($points)"
        )

        val msg = if (points == 0) {
            message.viewerHasNoPoints
                .replace("#USER#", commandEvent.viewer.displayName)
                .replace("#POINTS#", points.toString())
        } else {
            message.viewerHasPoints
                .replace("#USER#", commandEvent.viewer.displayName)
                .replace("#POINTS#", points.toString())
        }

        eventBus.send(SendMessageEvent(commandEvent.channel, msg))
    }
}
