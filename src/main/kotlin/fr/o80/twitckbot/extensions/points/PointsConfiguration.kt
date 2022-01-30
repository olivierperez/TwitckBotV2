package fr.o80.twitckbot.extensions.points

import fr.o80.twitckbot.system.bean.Badge
import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class PointsConfiguration(
    val channel: ChannelName,
    val privilegedBadges: Collection<Badge>,
    val i18n: PointsI18n
)

@Serializable
class PointsI18n(
    val destinationViewerDoesNotExist: String,
    val pointsTransferred: String,
    val notEnoughPoints: String,
    val viewerHasNoPoints: String,
    val viewerHasPoints: String,
)
