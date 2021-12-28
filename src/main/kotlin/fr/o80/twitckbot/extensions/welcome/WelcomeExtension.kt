package fr.o80.twitckbot.extensions.welcome

import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.time.TimeChecker
import fr.o80.twitckbot.service.time.TimeCheckerFactory
import fr.o80.twitckbot.service.twitch.TwitchApi
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.bean.Badge
import fr.o80.twitckbot.system.bean.Follower
import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.MessageEvent
import fr.o80.twitckbot.system.event.SendMessageEvent
import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.time.Duration

class WelcomeExtension(
    private val eventBus: EventBus,
    private val twitchApi: TwitchApi,
    private val logger: Logger,
    private val timeCheckerFactory: TimeCheckerFactory,
    private val stepsExecutor: StepsExecutor
) : Extension() {

    private val config: WelcomeConfiguration = readConfig("welcome.json")

    private val followers: List<Follower> by lazy {
        twitchApi.getFollowers(config.streamId)
    }

    private lateinit var welcomeTimeChecker: TimeChecker

    override suspend fun init() {
        logger.info("Initializing WelcomeExtension")

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
                val stepParam = StepParams(config.channel.name, viewer.displayName)
                stepsExecutor.execute(config.onWelcome, stepParam)
            }
        }
    }

    private suspend fun welcomeHost(channel: String, message: String) {
        eventBus.send(SendMessageEvent(channel, message))
    }

    private suspend fun welcomeViewer(channel: String, viewer: Viewer) {
        val randomMessage = pickMessage(viewer)
        eventBus.send(SendMessageEvent(channel, randomMessage))
    }

    private fun pickMessage(viewer: Viewer): String {
        val follower = viewer.login.getFollowerOrNull()
        return if (follower != null) {
            config.messages.forFollowers.random()
                .replace("#USER#", follower.user.displayName)
        } else {
            config.messages.forViewers.random()
                .replace("#USER#", viewer.displayName)
        }
    }

    private fun String.getFollowerOrNull(): Follower? {
        return followers.firstOrNull { follower -> follower.user.name == this }
    }
}
