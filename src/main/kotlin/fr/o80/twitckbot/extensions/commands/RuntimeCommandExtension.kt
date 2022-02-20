package fr.o80.twitckbot.extensions.commands

import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.connectable.chat.CoolDown
import fr.o80.twitckbot.service.connectable.chat.Priority
import fr.o80.twitckbot.service.help.Help
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SCOPE_STREAM = "stream"
const val SCOPE_PERMANENT = "permanent"

// TODO OPZ Extraire la partie gestion des commandes
class RuntimeCommandExtension @Inject constructor(
    private val storage: Storage,
    private val help: Help,
    private val eventBus: EventBus,
    loggerFactory: LoggerFactory,
) : Extension() {

    private val logger = loggerFactory.getLogger(RuntimeCommandExtension::class.java.simpleName)

    private val config: RuntimeCommandConfiguration

    private val namespace: String = RuntimeCommandExtension::class.java.name

    private val runtimeCommands = mutableMapOf<String, String?>()

    init {
        logger.info("Initializing")

        config = readConfig("runtime_commands.json")

        scope.launch {
            storage.getGlobalInfo(namespace)
                .filter { it.first.startsWith("Command//") }
                .forEach {
                    val commandTag = it.first.substring("Command//".length)
                    runtimeCommands[commandTag] = it.second
                    help.registerCommand(commandTag)
                }
        }

        scope.launch {
            eventBus.events.filterIsInstance<CommandEvent>().collect { event ->
                interceptCommandEvent(event)
            }
        }
    }

    private suspend fun interceptCommandEvent(
        commandEvent: CommandEvent
    ) {
        // !cmd stream !exo Aujourd'hui on développe des trucs funs
        // !cmd permanent !twitter Retrouvez-moi sur https://twitter.com/olivierperez
        when (commandEvent.command.tag) {
            "!cmd" -> handleAddCommand(commandEvent)
            in runtimeCommands.keys -> handleRegisteredCommand(commandEvent)
        }
        return
    }

    private suspend fun handleAddCommand(
        commandEvent: CommandEvent
    ) {
        if (commandEvent.viewer hasNoPrivilegesOf config.privilegedBadges) return

        val command = commandEvent.command
        val scope: String = command.options[0]
        val newCommand: String = command.options[1].lowercase()
        val message: String = command.options.subList(2, command.options.size).joinToString(" ")

        if (scope !in arrayOf(SCOPE_STREAM, SCOPE_PERMANENT)) {
            logger.error("Scope \"$scope\" non autorisé")
            return
        }
        if (!newCommand.startsWith("!")) {
            logger.error("Préfixe manquant pour $newCommand")
            return
        }

        registerRuntimeCommand(newCommand, scope, message)
        registerToHelper(newCommand)
        eventBus.send(
            SendMessageEvent(
                commandEvent.channel,
                "Commande $newCommand ajoutée",
                Priority.IMMEDIATE
            )
        )
    }

    private suspend fun handleRegisteredCommand(
        commandEvent: CommandEvent
    ) {
        runtimeCommands[commandEvent.command.tag]?.let { message ->
            eventBus.send(
                SendMessageEvent(
                    commandEvent.channel,
                    message,
                    Priority.IMMEDIATE,
                    CoolDown.ofSeconds(5)
                )
            )
        }
    }

    private suspend fun registerRuntimeCommand(newCommand: String, scope: String, message: String) {
        runtimeCommands[newCommand] = message

        if (scope == SCOPE_PERMANENT) {
            storage.putGlobalInfo(namespace, "Command//$newCommand", message)
        }
    }

    private fun registerToHelper(newCommand: String) {
        help.registerCommand(newCommand)
    }

}
