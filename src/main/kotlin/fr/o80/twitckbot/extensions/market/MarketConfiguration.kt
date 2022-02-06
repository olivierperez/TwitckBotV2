package fr.o80.twitckbot.extensions.market

import fr.o80.twitckbot.system.bean.ChannelName
import fr.o80.twitckbot.system.step.ActionStep
import kotlinx.serialization.Serializable

@Serializable
class MarketConfiguration(
    val channel: ChannelName,
    val i18n: MarketI18n,
    val products: List<MarketProduct>,
    val rewards: List<MarketReward>
)

@Serializable
class MarketI18n(
    val productNotFound: String,
    val usage: String,
    val weHaveThisProducts: String,
    val youDontHaveEnoughPoints: String
)

@Serializable
class MarketProduct(
    val name: String,
    val price: Int,
    val steps: List<ActionStep>
)

@Serializable
class MarketReward(
    val id: String,
    val steps: List<ActionStep>
)
