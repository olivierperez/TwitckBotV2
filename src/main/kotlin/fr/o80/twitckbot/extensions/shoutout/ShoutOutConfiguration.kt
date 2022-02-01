package fr.o80.twitckbot.extensions.shoutout

import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class ShoutOutConfiguration(
    val channel: ChannelName,
    val secondsBetweenTwoPromotions: Long,
    val daysSinceLastVideoToPromote: Long,
    val ignoredLogins: List<String>,
    val promotionMessages: List<String>,
    val i18n: ShoutOutI18n
)

@Serializable
class ShoutOutI18n(
    val usage: String,
    val noPointsEnough: String,
    val noAutoShoutOut: String,
    val shoutOutRecorded: String,
)
