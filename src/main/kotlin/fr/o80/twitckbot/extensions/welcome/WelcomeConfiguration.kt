package fr.o80.twitckbot.extensions.welcome

import fr.o80.twitckbot.system.bean.ChannelName
import fr.o80.twitckbot.system.step.ActionStep
import kotlinx.serialization.Serializable

@Serializable
class WelcomeConfiguration(
    val enabled: Boolean,
    val streamId: String,
    val channel: ChannelName,
    val secondsBetweenWelcomes: Long,
    val ignoreViewers: List<String>,
    val messages: WelcomeMessages,
    val reactTo: WelcomeReactTo,
    val onWelcome: List<ActionStep>
)

@Serializable
class WelcomeMessages(
    val forBroadcaster: List<String>,
    val forViewers: List<String>,
    val forFollowers: List<String>
)

@Serializable
class WelcomeReactTo(
    val joins: Boolean,
    val messages: Boolean,
    val commands: Boolean,
    val raids: Boolean,
)
