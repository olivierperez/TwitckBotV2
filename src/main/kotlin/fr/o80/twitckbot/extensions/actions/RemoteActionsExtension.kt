package fr.o80.twitckbot.extensions.actions

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.twitch.GetBroadcaster
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.EventBus
import javax.inject.Inject

@SessionScope
class RemoteActionsExtension @Inject constructor(
    eventBus: EventBus,
    store: RemoteActionStore,
    loggerFactory: LoggerFactory,
    getBroadcaster: GetBroadcaster
) : Extension() {

    private val logger = loggerFactory.getLogger(RemoteActionsExtension::class.java.simpleName)

    private val config: RemoteActionsConfiguration

    private val webSocket: UiWebSocket

    init {
        logger.info("Initializing")

        config = readConfig("remote_actions.json")

        webSocket = UiWebSocket(
            config.channel.name,
            config.actionsPort,
            store,
            eventBus,
            logger,
            getBroadcaster
        )

        webSocket.start()
    }
}
