package fr.o80.twitckbot.extensions.rewards

import fr.o80.twitckbot.system.bean.ChannelName
import kotlinx.serialization.Serializable

@Serializable
class RewardsConfiguration(
    val channel: ChannelName,
    val claim: RewardsClaim,
    val talk: RewardsTalk,
    val i18n: RewardsI18n
)

@Serializable
class RewardsClaim(
    val command: String = "!claim",
    val reward: Int,
    val secondsBetweenTwoClaims: Long,
    val image: String,
    val positiveSound: String = "positive",
    val negativeSound: String = "negative"
)

@Serializable
class RewardsTalk(
    val reward: Int,
    val secondsBetweenTwoTalkRewards: Long
)

@Serializable
class RewardsI18n(
    val viewerJustClaimed: String
)
