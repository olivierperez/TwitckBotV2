package fr.o80.twitckbot.extensions.commands

import fr.o80.twitckbot.system.bean.Badge
import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class RuntimeCommandConfiguration(
    val channel: ChannelName,
    val privilegedBadges: List<Badge>
)
