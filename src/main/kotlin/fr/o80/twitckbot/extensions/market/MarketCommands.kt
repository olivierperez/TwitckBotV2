package fr.o80.twitckbot.extensions.market

import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor

class MarketCommands(
    private val eventBus: EventBus,
    private val i18n: MarketI18n,
    private val logger: Logger,
    private val pointsExtension: Points,
    private val products: List<MarketProduct>,
    private val stepsExecutor: StepsExecutor
) {

    suspend fun interceptCommandEvent(commandEvent: CommandEvent) {
        when (commandEvent.command.tag) {
            "!buy" -> handleBuyCommand(commandEvent)
            "!market" -> handleMarketCommand(commandEvent)
        }
    }

    private suspend fun handleBuyCommand(commandEvent: CommandEvent) {
        if (commandEvent.command.options.isEmpty()) {
            // TODO CoolDown.ofSeconds(10)
            eventBus.send(SendMessageEvent(commandEvent.channel, i18n.usage))
            return
        }

        val product = products.firstOrNull { product ->
            product.name == commandEvent.command.options[0]
        }

        if (product == null) {
            // TODO CoolDown.ofSeconds(10)
            eventBus.send(SendMessageEvent(commandEvent.channel, i18n.productNotFound))
            return
        }

        doBuy(commandEvent, product, product.price)
    }

    private suspend fun handleMarketCommand(commandEvent: CommandEvent) {
        val productNames = products.joinToString(", ") { it.name }
        val message = i18n.weHaveThisProducts.replace("#PRODUCTS#", productNames)
        // TODO CoolDown.ofSeconds(15)
        eventBus.send(SendMessageEvent(commandEvent.channel, message))
    }

    private suspend fun doBuy(
        commandEvent: CommandEvent,
        product: MarketProduct,
        price: Int
    ) {
        if (pointsExtension.consumePoints(commandEvent.viewer.login, price)) {
            try {
                logger.info("${commandEvent.viewer.displayName} just spend $price points for ${product.name}")

                val params = StepParams.fromCommand(commandEvent, skipOptions = 1)
                stepsExecutor.execute(product.steps, params)
            } catch (e: Exception) {
                logger.error("Failed to execute purchase!", e)
                pointsExtension.addPoints(commandEvent.viewer.login, price)
            }
        } else {
            eventBus.send(
                SendMessageEvent(
                    commandEvent.channel,
                    i18n.youDontHaveEnoughPoints
                        .replace("#USER#", commandEvent.viewer.displayName)
                )
            )
        }
    }

}
