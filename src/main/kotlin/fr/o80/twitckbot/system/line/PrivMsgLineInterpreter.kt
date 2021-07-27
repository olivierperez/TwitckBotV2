package fr.o80.twitckbot.system.line

import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.MessageEvent
import javax.inject.Inject

class PrivMsgLineInterpreter @Inject constructor(
    private val eventBus: EventBus,
//    private val commandParser: CommandParser,
//    private val emoteDownloader: EmoteDownloader
) : LineInterpreter {

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
            /*val command = commandParser.parse(message)

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
            }*/
        }
    }

    /*private fun dispatchBits(
        channel: String,
        bits: Int,
        viewer: Viewer
    ) {
        bitsDispatcher.dispatch(
            BitsEvent(messenger, channel, bits, viewer)
        )
    }

    private fun dispatchCommand(
        channel: String,
        command: Command,
        tags: Tags,
        viewer: Viewer
    ) {
        commandDispatcher.dispatch(
            CommandEvent(messenger, channel, command, tags.bits, viewer)
        )
    }

    private fun dispatchEmotes(
        channel: String,
        emotes: List<String>,
        emoteOnly: Boolean,
        message: String,
        viewer: Viewer
    ) {
        emotesDispatcher.dispatch(
            EmotesEvent(messenger, channel, message, emotes, emoteOnly, viewer)
        )
    }

    private fun dispatchMessage(
        channel: String,
        msg: String,
        viewer: Viewer
    ) {
        messageDispatcher.dispatch(
            MessageEvent(messenger, channel, msg, viewer)
        )
    }

    private fun dispatchReward(
        channel: String,
        rewardId: String,
        message: String,
        viewer: Viewer
    ) {
        rewardDispatcher.dispatch(
            RewardEvent(messenger, channel, rewardId, message, viewer)
        )
    }*/
}
