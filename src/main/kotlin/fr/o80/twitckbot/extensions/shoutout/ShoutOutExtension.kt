package fr.o80.twitckbot.extensions.shoutout

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.help.Help
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.service.time.TimeChecker
import fr.o80.twitckbot.service.time.TimeCheckerFactory
import fr.o80.twitckbot.service.twitch.TwitchApi
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.bean.Video
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.MessageEvent
import fr.o80.twitckbot.system.event.SendMessageEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@SessionScope
class ShoutOutExtension @Inject constructor(
    private val eventBus: EventBus,
    private val twitchApi: TwitchApi,
    help: Help,
    loggerFactory: LoggerFactory,
    points: Points,
    sound: Sound,
    storage: Storage,
    timeCheckerFactory: TimeCheckerFactory,
) : Extension() {

    private val logger = loggerFactory.getLogger(ShoutOutExtension::class.java.simpleName)

    private val config: ShoutOutConfiguration

    private val promotionTimeChecker: TimeChecker

    private val command: ShoutOutCommand

    init {
        logger.info("Initializing")
        help.registerCommand(SHOUT_OUT_COMMAND)

        config = readConfig("shout_out.json")

        promotionTimeChecker = timeCheckerFactory.create(
            namespace = ShoutOutExtension::class,
            "promotedAt",
            Duration.ofSeconds(config.secondsBetweenTwoPromotions)
        )

        command = ShoutOutCommand(
            config,
            storage,
            sound,
            points,
            eventBus
        )

        scope.launch {
            eventBus.events.filterIsInstance<MessageEvent>().collect { event ->
                interceptMessageEvent(event)
            }
        }
        scope.launch {
            eventBus.events.filterIsInstance<CommandEvent>().collect { event ->
                command.interceptCommandEvent(event)
            }
        }
    }

    private suspend fun interceptMessageEvent(messageEvent: MessageEvent): MessageEvent {
        if (config.channel.name != messageEvent.channel)
            return messageEvent

        if (messageEvent.viewer.login in config.ignoredLogins) {
            return messageEvent
        }

        promotionTimeChecker.executeIfNotCooldown(messageEvent.viewer.login) {
            promoteViewer(messageEvent)
        }

        return messageEvent
    }

    private suspend fun promoteViewer(messageEvent: MessageEvent) {
        val lastVideo = twitchApi.getVideos(messageEvent.viewer.userId, 1)
            .filter {
                (it.publishedAt + Duration.ofDays(config.daysSinceLastVideoToPromote))
                    .isAfter(LocalDateTime.now())
            }
            .takeIf { it.isNotEmpty() }
            ?.first()
            ?: return

        val randomMessage = config.promotionMessages.random().formatViewer(messageEvent, lastVideo)
        // TODO Handle CoolDown/Importance instead of call below -> messenger.sendWhenAvailable(messageEvent.channel, randomMessage, Importance.HIGH)
        eventBus.send(SendMessageEvent(messageEvent.channel, randomMessage))
    }

    private fun String.formatViewer(messageEvent: MessageEvent, video: Video): String =
        this.replace("#USER#", messageEvent.viewer.displayName)
            .replace("#URL#", video.url)

}
