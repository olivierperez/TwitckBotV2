package fr.o80.twitckbot.extensions.help

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.connectable.chat.CoolDown
import fr.o80.twitckbot.service.connectable.chat.Priority
import fr.o80.twitckbot.service.help.Help
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@SessionScope
class HelpExtension @Inject constructor(
    private val eventBus: EventBus,
    loggerFactory: LoggerFactory,
) : Help, Extension() {

    private val logger = loggerFactory.getLogger(HelpExtension::class.java.simpleName)

    private val config: HelpConfiguration

    init {
        logger.info("Initializing")

        config = readConfig("help.json")

        scope.launch {
            eventBus.events.filterIsInstance<CommandEvent>().collect { event ->
                interceptCommandEvent(event)
            }
        }
    }

    private val commands = mutableMapOf<String, String?>().apply {
        putAll(config.commands)
    }

    override fun registerCommand(command: String) {
        commands[command] = null
    }

    private suspend fun interceptCommandEvent(
        commandEvent: CommandEvent
    ): CommandEvent {
        if (config.channel.name != commandEvent.channel)
            return commandEvent

        when (commandEvent.command.tag) {
            "!help" -> {
                eventBus.send(createHelpMessage(commands.keys))
            }
            in commands.keys -> {
                executeCommand(commandEvent.command.tag)
            }
        }

        return commandEvent
    }

    private suspend fun executeCommand(tag: String) {
        commands[tag]?.let { message ->
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

    private fun createHelpMessage(commands: Collection<String>): SendMessageEvent {
        return when {
            commands.isEmpty() -> {
                SendMessageEvent(
                    config.channel.name,
                    "Je ne sais rien faire O_o du moins pour l'instant...",
                    Priority.IMMEDIATE,
                    CoolDown.ofSeconds(60)
                )
            }
            else -> {
                val commandsExamples = commands.joinToString(", ")
                SendMessageEvent(
                    config.channel.name,
                    "Je sais faire un paquet de choses, par exemple : $commandsExamples",
                    Priority.IMMEDIATE,
                    CoolDown.ofSeconds(60)
                )
            }
        }
    }
}
