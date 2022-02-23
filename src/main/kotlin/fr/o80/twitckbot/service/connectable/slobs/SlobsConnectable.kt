package fr.o80.twitckbot.service.connectable.slobs

import fr.o80.slobs.AsyncSlobsClient
import fr.o80.slobs.SlobsClient
import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.connectable.Connectable
import fr.o80.twitckbot.service.connectable.ConnectableStatus
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SlobsConfigEvent
import fr.o80.twitckbot.system.event.SwitchSceneEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@SessionScope
class SlobsConnectable @Inject constructor(
    private val eventBus: EventBus,
    loggerFactory: LoggerFactory
) : Connectable {

    private val logger = loggerFactory.getLogger(SlobsConnectable::class.java.simpleName)

    override val name: String
        get() = "OBS"

    override val icon: String
        get() = "ic_stream.png"

    private val _state = MutableStateFlow(ConnectableStatus.NOT_CONNECTED)
    override val status: Flow<ConnectableStatus> = _state

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val slobsClient: SlobsClient

    init {
        val config: SlobsConfiguration = readConfig("slobs.json")

        slobsClient = AsyncSlobsClient(
            config.host,
            config.port,
            config.token
        )
    }

    override suspend fun connect() {
        try {
            _state.value = ConnectableStatus.CONNECTING
            slobsClient.connect()
            _state.value = ConnectableStatus.CONNECTED

            scope.launch(Dispatchers.IO) { slobsClient.listenSceneSwitching() }
            scope.launch { eventBus.forwardSwitchSceneEventsTo(slobsClient) }

            eventBus.sendSlobsConfig()
        } catch (e: Exception) {
            _state.value = ConnectableStatus.NOT_CONNECTED
        }
    }

    private suspend fun SlobsClient.listenSceneSwitching() {
        while (true) {
            this.onSceneSwitched().consumeEach { scene ->
                logger.info("Scene switched: ${scene.name}")
                eventBus.sendSlobsConfig()
            }
        }
    }

    private suspend fun EventBus.forwardSwitchSceneEventsTo(slobsClient: SlobsClient) {
        this.events.filterIsInstance<SwitchSceneEvent>().collect { event ->
            slobsClient.switchTo(event.sceneId)
        }
    }

    private suspend fun EventBus.sendSlobsConfig() {
        this.send(
            SlobsConfigEvent(
                scenes = slobsClient.getScenes(),
                activeScene = slobsClient.getActiveScene()
            )
        )
    }
}
