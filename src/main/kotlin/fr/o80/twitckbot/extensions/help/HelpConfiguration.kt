package fr.o80.twitckbot.extensions.help

import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class HelpConfiguration(
    val channel: ChannelName,
    val commands: Map<String, String>
)
