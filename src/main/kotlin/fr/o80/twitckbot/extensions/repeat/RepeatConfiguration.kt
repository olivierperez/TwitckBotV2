package fr.o80.twitckbot.extensions.repeat

import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class RepeatConfiguration(
    val channel: ChannelName,
    val secondsBetweenRepeatedMessages: Long,
    val messages: List<String>,
)
