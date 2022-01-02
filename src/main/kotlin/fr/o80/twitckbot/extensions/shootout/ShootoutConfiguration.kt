package fr.o80.twitckbot.extensions.shootout

import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class ShootoutConfiguration(
    val channel: ChannelName,
    val secondsBetweenTwoPromotions: Long,
    val daysSinceLastVideoToPromote: Long,
    val ignoredLogins: List<String>,
    val promotionMessages: List<String>,
    val i18n: ShootoutI18n
)

@Serializable
class ShootoutI18n(
    val usage: String,
    val noPointsEnough: String,
    val noAutoShootout: String,
    val shootoutRecorded: String,
)
