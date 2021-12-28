package fr.o80.twitckbot.system.line

import fr.o80.twitckbot.internal.emote.EmoteDownloader
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.system.bean.Command
import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.command.CommandParser
import fr.o80.twitckbot.system.event.BitsEvent
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EmotesEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.MessageEvent
import fr.o80.twitckbot.system.event.RewardEvent
import javax.inject.Inject

class PrivMsgLineInterpreter @Inject constructor(
    private val eventBus: EventBus,
    private val commandParser: CommandParser,
    loggerFactory: LoggerFactory,
    private val emoteDownloader: EmoteDownloader
) : LineInterpreter {

    private val logger = loggerFactory.getLogger(PrivMsgLineInterpreter::class)

    private val regex =
        "^@([^ ]+) :([^!]+)![^@]+@[^.]+\\.tmi\\.twitch\\.tv PRIVMSG (#[^ ]+) :(.+)$".toRegex()

    override suspend fun handle(line: String) {
        regex.find(line)?.let { matchResult ->
            val tags = Tags.from(matchResult.groupValues[1])
            val user = matchResult.groupValues[2]
            val channel = matchResult.groupValues[3]
            val message = matchResult.groupValues[4]

            val viewer = Viewer(
                login = user,
                displayName = tags.displayName,
                badges = tags.badges,
                userId = tags.userId,
                color = tags.color
            )

            eventBus.send(MessageEvent(channel, viewer, message))
            val command = commandParser.parse(message)

            tags.bits?.let { bits ->
                logger.debug("Bits have been detected: \n=>$viewer\n=>$tags\n===================")
                dispatchBits(channel, bits, viewer)
            }

            tags.customRewardId?.let { rewardId ->
                logger.debug("Reward claimed: $rewardId\n=>$viewer\n=>$tags\n===================")
                dispatchReward(channel, rewardId, message, viewer)
            }

            tags.emotes?.let { emotes ->
                logger.debug("Emotes incoming: $emotes")
                emoteDownloader.download(emotes) {
                    dispatchEmotes(channel, emotes, tags.emoteOnly, message, viewer)
                }
            }

            when {
                command != null -> dispatchCommand(channel, command, tags, viewer)
                else -> dispatchMessage(channel, message, viewer)
            }
        }
    }

    private suspend fun dispatchBits(
        channel: String,
        bits: Int,
        viewer: Viewer
    ) {
        eventBus.send(
            BitsEvent(channel, bits, viewer)
        )
    }

    private suspend fun dispatchCommand(
        channel: String,
        command: Command,
        tags: Tags,
        viewer: Viewer
    ) {
        eventBus.send(
            CommandEvent(channel, command, tags.bits, viewer)
        )
    }

    private suspend fun dispatchEmotes(
        channel: String,
        emotes: List<String>,
        emoteOnly: Boolean,
        message: String,
        viewer: Viewer
    ) {
        eventBus.send(
            EmotesEvent(channel, message, emotes, emoteOnly, viewer)
        )
    }

    private suspend fun dispatchMessage(
        channel: String,
        msg: String,
        viewer: Viewer
    ) {
        eventBus.send(
            MessageEvent(channel, viewer, msg)
        )
    }

    private suspend fun dispatchReward(
        channel: String,
        rewardId: String,
        message: String,
        viewer: Viewer
    ) {
        eventBus.send(
            RewardEvent(channel, rewardId, message, viewer)
        )
    }
}
