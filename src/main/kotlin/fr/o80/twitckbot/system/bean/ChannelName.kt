package fr.o80.twitckbot.system.bean

import fr.o80.twitckbot.system.json.ChannelNameSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ChannelNameSerializer::class)
class ChannelName(val name: String)
