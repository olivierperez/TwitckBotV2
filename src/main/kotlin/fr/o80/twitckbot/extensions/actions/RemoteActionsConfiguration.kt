package fr.o80.twitckbot.extensions.actions

import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class RemoteActionsConfiguration(
    val channel: ChannelName,
    val actionsPort: Int
)
