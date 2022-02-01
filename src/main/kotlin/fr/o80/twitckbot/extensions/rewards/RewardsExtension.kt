package fr.o80.twitckbot.extensions.rewards

import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.service.time.TimeChecker
import fr.o80.twitckbot.service.time.TimeCheckerFactory
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.MessageEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

class RewardsExtension @Inject constructor(
    private val points: Points,
    eventBus: EventBus,
    loggerFactory: LoggerFactory,
    sound: Sound,
    timeCheckerFactory: TimeCheckerFactory
//    help: HelpExtension?
) : Extension() {

    private val logger = loggerFactory.getLogger(RewardsExtension::class.java.simpleName)

    private val config: RewardsConfiguration

    private val talkingTimeChecker: TimeChecker

    private val commands: RewardsCommands

    init {
        logger.info("Initializing")

        config = readConfig("rewards.json")

        commands = RewardsCommands(
            config.channel,
            config.claim,
            config.i18n,
            timeCheckerFactory.createClaimTimeChecker(),
            points,
            sound
        )

        talkingTimeChecker = timeCheckerFactory.create(
            namespace = RewardsExtension::class,
            flag = "talkedRewardedAt",
            interval = Duration.ofSeconds(config.talk.secondsBetweenTwoTalkRewards)
        )

        scope.launch {
            eventBus.events.filterIsInstance<MessageEvent>().collect { event ->
                rewardTalkativeViewers(event)
            }
        }

        scope.launch {
            eventBus.events.filterIsInstance<CommandEvent>().collect { event ->
                commands.interceptCommandEvent(event)
            }
        }

//        help?.registerCommand(claimConfig.command)
    }

    private fun TimeCheckerFactory.createClaimTimeChecker() =
        this.create(
            namespace = RewardsExtension::class,
            flag = "claimedAt",
            interval = Duration.ofSeconds(config.claim.secondsBetweenTwoClaims)
        )

    private suspend fun rewardTalkativeViewers(messageEvent: MessageEvent) {
        if (config.talk.reward == 0) return

        talkingTimeChecker.executeIfNotCooldown(messageEvent.viewer.login) {
            points.addPoints(messageEvent.viewer.login, config.talk.reward)
        }
    }

}

