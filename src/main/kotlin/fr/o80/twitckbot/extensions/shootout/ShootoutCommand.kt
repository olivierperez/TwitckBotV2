package fr.o80.twitckbot.extensions.shootout

import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent

const val RECORDING_COST = 200
const val SHOOTOUT_COMMAND = "!shootout"
const val SHOOTOUT_COST = 50

class ShootoutCommand(
    private val config: ShootoutConfiguration,
    private val storage: Storage,
    private val sound: Sound,
//    private val points: PointsExtension?,
    private val eventBus: EventBus
) {

    private val namespace: String = ShootoutExtension::class.java.name

    suspend fun interceptCommandEvent(commandEvent: CommandEvent): CommandEvent {
        when (commandEvent.command.tag) {
            SHOOTOUT_COMMAND -> handleShootoutCommand(commandEvent)
        }
        return commandEvent
    }

    suspend fun interceptWhisperCommandEvent(
        commandEvent: CommandEvent
    ): CommandEvent {
        when (commandEvent.command.tag) {
            SHOOTOUT_COMMAND -> shootout(commandEvent)
        }
        return commandEvent
    }

    private suspend fun handleShootoutCommand(commandEvent: CommandEvent) {
        when (commandEvent.command.options.size) {
            0 -> showUsage(commandEvent)
            1 -> shootout(commandEvent)
            else -> recordShootout(commandEvent)
        }
    }

    private suspend fun showUsage(commandEvent: CommandEvent) {
        eventBus.send(SendMessageEvent(commandEvent.channel, config.i18n.usage))
    }

    private suspend fun recordShootout(commandEvent: CommandEvent) {
        val viewerLogin = commandEvent.viewer.login
        val shootoutLogin = commandEvent.command.options[0]
        val message = commandEvent.command.options.drop(1).joinToString(" ")

        if (viewerLogin == shootoutLogin) {
            val errorMessage = config.i18n.noAutoShootout.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(config.channel.name, errorMessage))
            sound.playNegative()
            return
        }

        // TODO Points
        /*if (points != null && !points.consumePoints(viewerLogin, RECORDING_COST)) {
            val errorMessage = i18n.noPointsEnough.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(channel, errorMessage))
            sound.playNegative()
            return
        }*/

        if (storage.hasUserInfo(shootoutLogin)) {
            storage.putUserInfo(shootoutLogin, namespace, SHOOTOUT_COMMAND, message)
            eventBus.send(SendMessageEvent(config.channel.name, config.i18n.shootoutRecorded))
        }
    }

    private suspend fun shootout(commandEvent: CommandEvent) {
        val viewerLogin = commandEvent.viewer.login
        val shootoutLogin = commandEvent.command.options[0]

        if (viewerLogin == shootoutLogin) {
            val errorMessage = config.i18n.noAutoShootout.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(config.channel.name, errorMessage))
            sound.playNegative()
            return
        }

        // TODO Points
        /*
        if (points != null && !points.consumePoints(viewerLogin, SHOOTOUT_COST)) {
            val message = i18n.noPointsEnough.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(channel, message))
            return
        }*/

        if (storage.hasUserInfo(shootoutLogin)) {
            storage.getUserInfo(shootoutLogin, namespace, SHOOTOUT_COMMAND)?.let { message ->
                // TODO Send with CoolDown
                eventBus.send(SendMessageEvent(commandEvent.channel, message))
            }
        }
    }
}
