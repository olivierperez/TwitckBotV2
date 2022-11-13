package fr.o80.twitckbot.extensions.welcome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.connectable.chat.Priority
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.time.TimeChecker
import fr.o80.twitckbot.service.time.TimeCheckerFactory
import fr.o80.twitckbot.service.twitch.TwitchApi
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.ExtensionBloc
import fr.o80.twitckbot.system.bean.Badge
import fr.o80.twitckbot.system.bean.Follower
import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.MessageEvent
import fr.o80.twitckbot.system.event.SendMessageEvent
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@SessionScope
class WelcomeExtension @Inject constructor(
    private val eventBus: EventBus,
    private val stepsExecutor: StepsExecutor,
    loggerFactory: LoggerFactory,
    timeCheckerFactory: TimeCheckerFactory,
    twitchApi: TwitchApi,
) : Extension() {

    private val logger = loggerFactory.getLogger(WelcomeExtension::class.java.simpleName)

    private val config: WelcomeConfiguration

    private val followers: List<Follower>

    private val welcomeTimeChecker: TimeChecker

    private val lastWelcomed: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    override val blocs: List<ExtensionBloc>
        get() = listOf(WelcomeBloc())

    inner class WelcomeBloc : ExtensionBloc {
        @Composable
        override fun render(modifier: Modifier) {
            val lastWelcomed by lastWelcomed.collectAsState()

            WelcomeContent(modifier, lastWelcomed)
        }

    }

    init {
        logger.info("Initializing")

        config = readConfig("welcome.json")

        followers = twitchApi.getFollowers(config.streamId)

        welcomeTimeChecker = timeCheckerFactory.create(
            namespace = WelcomeExtension::class,
            flag = "welcomedAt",
            interval = Duration.ofSeconds(config.secondsBetweenWelcomes)
        )

        if (config.enabled) {
            if (config.reactTo.messages) {
                scope.launch {
                    eventBus.events.filterIsInstance<MessageEvent>().collect { event ->
                        handleNewViewer(event.channel, event.viewer)
                    }
                }
            }
        }
    }

    private suspend fun handleNewViewer(channel: String, viewer: Viewer) {
        if (config.channel.name != channel)
            return

        if (config.ignoreViewers.any { viewer.login.equals(it, true) }) {
            return
        }

        if (config.messages.forBroadcaster.isNotEmpty() && Badge.BROADCASTER in viewer.badges) {
            welcomeTimeChecker.executeIfNotCooldown(viewer.login) {
                val message = config.messages.forBroadcaster.random()
                    .replace("#USER#", viewer.displayName)
                welcomeHost(channel, message)
            }
        } else {
            welcomeTimeChecker.executeIfNotCooldown(viewer.login) {
                welcomeViewer(channel, viewer)
                val stepParam = StepParams(config.channel.name, viewer)
                stepsExecutor.execute(config.onWelcome, stepParam)
                lastWelcomed.update { (it + viewer.login).takeLast(5) }
            }
        }
    }

    private suspend fun welcomeHost(channel: String, message: String) {
        eventBus.send(SendMessageEvent(channel, message, Priority.LOW))
    }

    private suspend fun welcomeViewer(channel: String, viewer: Viewer) {
        val randomMessage = pickMessage(viewer)
        eventBus.send(SendMessageEvent(channel, randomMessage, Priority.LOW))
    }

    private fun pickMessage(viewer: Viewer): String {
        val follower = viewer.getFollowerOrNull()
        return if (follower != null) {
            config.messages.forFollowers.random()
                .replace("#USER#", follower.fromName)
        } else {
            config.messages.forViewers.random()
                .replace("#USER#", viewer.displayName)
        }
    }

    private fun Viewer.getFollowerOrNull(): Follower? {
        return followers.firstOrNull { follower -> follower.fromId == this.userId }
    }
}
