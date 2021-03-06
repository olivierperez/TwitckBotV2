package fr.o80.twitckbot.extensions.shoutout

import fr.o80.twitckbot.service.connectable.chat.CoolDown
import fr.o80.twitckbot.service.connectable.chat.Priority
import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent

const val RECORDING_COST = 200
const val SHOUT_OUT_COMMAND = "!shoutout"
const val SHOUT_OUT_COST = 50

class ShoutOutCommand(
    private val config: ShoutOutConfiguration,
    private val storage: Storage,
    private val sound: Sound,
    private val points: Points,
    private val eventBus: EventBus
) {

    private val namespace: String = ShoutOutExtension::class.java.name

    suspend fun interceptCommandEvent(commandEvent: CommandEvent): CommandEvent {
        when (commandEvent.command.tag) {
            SHOUT_OUT_COMMAND -> handleShoutOutCommand(commandEvent)
        }
        return commandEvent
    }

    private suspend fun handleShoutOutCommand(commandEvent: CommandEvent) {
        when (commandEvent.command.options.size) {
            0 -> showUsage(commandEvent)
            1 -> shoutOut(commandEvent)
            else -> recordShoutOut(commandEvent)
        }
    }

    private suspend fun showUsage(commandEvent: CommandEvent) {
        eventBus.send(SendMessageEvent(commandEvent.channel, config.i18n.usage, Priority.IMMEDIATE))
    }

    private suspend fun recordShoutOut(commandEvent: CommandEvent) {
        val viewerLogin = commandEvent.viewer.login
        val shoutOutLogin = commandEvent.command.options[0]
        val message = commandEvent.command.options.drop(1).joinToString(" ")

        if (viewerLogin == shoutOutLogin) {
            val errorMessage = config.i18n.noAutoShoutOut.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(config.channel.name, errorMessage, Priority.IMMEDIATE))
            sound.playNegative()
            return
        }

        if (!points.consumePoints(viewerLogin, RECORDING_COST)) {
            val errorMessage = config.i18n.noPointsEnough.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(commandEvent.channel, errorMessage, Priority.IMMEDIATE))
            sound.playNegative()
            return
        }

        if (storage.hasUserInfo(shoutOutLogin)) {
            storage.putUserInfo(shoutOutLogin, namespace, SHOUT_OUT_COMMAND, message)
            eventBus.send(
                SendMessageEvent(
                    config.channel.name,
                    config.i18n.shoutOutRecorded,
                    Priority.IMMEDIATE
                )
            )
        }
    }

    private suspend fun shoutOut(commandEvent: CommandEvent) {
        val viewerLogin = commandEvent.viewer.login
        val shoutOutLogin = commandEvent.command.options[0]

        if (viewerLogin == shoutOutLogin) {
            val errorMessage = config.i18n.noAutoShoutOut.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(config.channel.name, errorMessage, Priority.IMMEDIATE))
            sound.playNegative()
            return
        }

        if (!points.consumePoints(viewerLogin, SHOUT_OUT_COST)) {
            val message = config.i18n.noPointsEnough.replace("#USER#", viewerLogin)
            eventBus.send(SendMessageEvent(commandEvent.channel, message, Priority.IMMEDIATE))
            return
        }

        if (storage.hasUserInfo(shoutOutLogin)) {
            storage.getUserInfo(shoutOutLogin, namespace, SHOUT_OUT_COMMAND)?.let { message ->
                eventBus.send(
                    SendMessageEvent(
                        config.channel.name,
                        message,
                        Priority.IMMEDIATE,
                        CoolDown.ofSeconds(60)
                    )
                )
            }
        }
    }
}
