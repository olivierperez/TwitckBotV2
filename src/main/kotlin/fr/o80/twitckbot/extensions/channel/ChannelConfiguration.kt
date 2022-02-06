package fr.o80.twitckbot.extensions.channel

import fr.o80.twitckbot.system.bean.ChannelName
import fr.o80.twitckbot.system.step.ActionStep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ChannelConfiguration(
    val channel: ChannelName,
    val commands: Map<String, List<ActionStep>>,
    @SerialName("follows")
    val followsStep: List<ActionStep>,
    @SerialName("bits")
    val bitsStep: List<ActionStep>
)
