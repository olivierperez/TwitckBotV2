package fr.o80.twitckbot.extensions.welcome

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.connectable.chat.Priority
import fr.o80.twitckbot.service.log.LoggerFactory
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
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
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
