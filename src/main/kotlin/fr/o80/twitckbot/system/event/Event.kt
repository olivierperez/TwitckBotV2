package fr.o80.twitckbot.system.event

import fr.o80.twitckbot.system.bean.Command
import fr.o80.twitckbot.system.bean.NewFollowers
import fr.o80.twitckbot.system.bean.Viewer

sealed interface Event

data class BitsEvent(
    val channel: String,
    val bits: Int,
    val viewer: Viewer
): Event

data class CommandEvent(
    val channel: String,
    val command: Command,
    val bits: Int?,
    val viewer: Viewer
): Event

data class EmotesEvent(
    val channel: String,
    val message: String,
    val emotes: List<String>,
    val emoteOnly: Boolean,
    val viewer: Viewer
): Event

data class FollowsEvent(
    val followers: NewFollowers
)

class MessageEvent(
    val channel: String,
    val viewer: Viewer,
    val message: String
) : Event

data class RewardEvent(
    val channel: String,
    val rewardId: String,
    val message: String,
    val viewer: Viewer
): Event

class SendMessageEvent(
    val channel: String,
    val message: String,
    // TODO coolDown: CoolDown,
    // TODO priority: Priority,
) : Event

class SendWhisperEvent(
    val channel: String,
    val recipient: String,
    val message: String,
) : Event

class WhisperEvent(
    val destination: String,
    val viewer: Viewer,
    val message: String
) : Event