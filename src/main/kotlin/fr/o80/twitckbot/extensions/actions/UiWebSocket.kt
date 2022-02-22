package fr.o80.twitckbot.extensions.actions

import fr.o80.slobs.SlobsClient
import fr.o80.twitckbot.extensions.actions.model.Config
import fr.o80.twitckbot.extensions.actions.model.RemoteAction
import fr.o80.twitckbot.extensions.actions.model.Scene
import fr.o80.twitckbot.extensions.actions.model.Status
import fr.o80.twitckbot.service.connectable.chat.CoolDown
import fr.o80.twitckbot.service.connectable.chat.Priority
import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.service.twitch.GetBroadcaster
import fr.o80.twitckbot.system.bean.Command
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EmotesEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Duration

private val webSocketSerializer = Json

private inline fun <reified T : Any> String.parse(): T {
    return webSocketSerializer.decodeFromString<T>(this)
}

class UiWebSocket(
    private val channel: String,
    private val port: Int,
    private val store: RemoteActionStore,
    private val slobsClient: SlobsClient,
    private val eventBus: EventBus,
    private val logger: Logger,
    private val getBroadcaster: GetBroadcaster
) {

    private val sessions = mutableListOf<DefaultWebSocketServerSession>()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            slobsClient.connect()

            launch(Dispatchers.IO) {
                while (true) {
                    slobsClient.onSceneSwitched().consumeEach { scene ->
                        logger.info("Scene switched: ${scene.name}")
                        val statusJson = getStatusJson(scene.id)
                        dispatch { session ->
                            session.send("""Status:$statusJson""")
                        }
                    }
                }
            }
        }
    }

    fun start() {
        embeddedServer(Netty, port) {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                webSocket("/actions") {
                    try {
                        sessions += this
                        while (true) {
                            when (val frame = incoming.receive()) {
                                is Frame.Binary -> logger.warn("Binary not handled")
                                is Frame.Close -> onClose(this, frame)
                                is Frame.Ping -> logger.warn("Ping not handled")
                                is Frame.Pong -> logger.warn("Pong not handled")
                                is Frame.Text -> onText(this, frame)
                            }
                        }
                    } catch (e: Exception) {
                        logger.error("Error while listening web socket", e)
                    } finally {
                        sessions.remove(this)
                    }
                }
            }
        }.start(wait = false)
    }

    private suspend fun onText(session: DefaultWebSocketServerSession, frame: Frame.Text) {
        val request = frame.readText()
        when {
            request == "GetConfig" -> {
                onConfigRequested(session)
            }
            request.startsWith("AddAction:") -> {
                val newActionJson = request.removePrefix("AddAction:")
                onNewAction(newActionJson)
            }
            request.startsWith("GetImage:") -> {
                val imageName = request.removePrefix("GetImage:")
                onImageRequested(session, imageName)
            }
            request.startsWith("Command:") -> {
                val command = request.removePrefix("Command:")
                onCommand(command)
            }
            request.startsWith("Message:") -> {
                val command = request.removePrefix("Message:")
                onMessage(command)
            }
            request.startsWith("Scene:") -> {
                val sceneId = request.removePrefix("Scene:")
                onScene(sceneId)
            }
            request.startsWith("Emotes:") -> {
                val emotes = request.removePrefix("Emotes:")
                onEmotes(emotes)
            }
            else -> {
                logger.debug("Someone requested something weird: $request")
                session.send(Frame.Text("Unknown request"))
            }
        }
    }

    private suspend fun onEmotes(emotes: String) {
        eventBus.send(
            EmotesEvent(
                channel = "",
                message = "",
                emotes = emotes.split("/").map { "$it:0" },
                emoteOnly = true,
                viewer = getBroadcaster()
            )
        )
    }

    private suspend fun onConfigRequested(session: DefaultWebSocketServerSession) {
        session.send("Config:${getConfigJson()}")
        session.send("Status:${getStatusJson(slobsClient.getActiveScene().id)}")
    }

    private suspend fun onImageRequested(
        session: DefaultWebSocketServerSession,
        imageName: String
    ) {
        logger.trace("Image has been requested: $imageName")
        val file = File(imageName)
        val bytes = file.readBytes()

        val data = merge(
            imageName.toByteArray(),
            "?#:|".toByteArray(),
            bytes
        )

        session.send(Frame.Binary(true, data))
    }

    private suspend fun getConfigJson(): String {
        val actions = store.getActions()
        val scenes = slobsClient.getScenes().map { Scene(it.id, it.name) }

        val config = Config(actions, scenes)
        return webSocketSerializer.encodeToString(config)
    }

    private fun getStatusJson(sceneId: String): String {
        val status = Status(currentSceneId = sceneId)
        return webSocketSerializer.encodeToString(status)
    }

    private suspend fun onNewAction(newActionJson: String) {
        logger.debug("Someone requested the adding of action: $newActionJson")
        val action: RemoteAction = newActionJson.parse()
        store.addAction(action)

        dispatch { otherSession ->
            otherSession.send(Frame.Text("Actions:${getActionsJson()}"))
        }
    }

    private suspend fun onCommand(commandTag: String) {
        logger.info("Command received from UI: $commandTag")
        // TODO Parser les options plutôt que de partir du principe qu'il n'y en a pas
        eventBus.send(
            CommandEvent(
                channel = "", // TODO
                command = Command(commandTag, emptyList()),
                bits = 0,
                viewer = getBroadcaster()
            )
        )
    }

    private suspend fun onMessage(message: String) {
        logger.debug("Message received from UI: $message")
        eventBus.send(
            SendMessageEvent(channel, message, Priority.IMMEDIATE, CoolDown.ofSeconds(1))
        )
    }

    private suspend fun onScene(sceneId: String) {
        slobsClient.switchTo(sceneId)
    }

    private suspend fun getActionsJson(): String {
        return webSocketSerializer.encodeToString(store.getActions())
    }

    private suspend fun dispatch(function: suspend (DefaultWebSocketServerSession) -> Unit) {
        sessions.forEach {
            function(it)
        }
    }

    private fun onClose(session: DefaultWebSocketServerSession, close: Frame.Close) {
        logger.debug("Closed: ${close.readReason().toString()}")
        sessions.remove(session)
    }

}
