package fr.o80.twitckbot.extensions.market

import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.system.event.RewardEvent
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor

class MarketRewards(
    private val rewards: List<MarketReward>,
    private val stepsExecutor: StepsExecutor,
    private val logger: Logger
) {

    suspend fun interceptRewardEvent(rewardEvent: RewardEvent) {
        logger.debug("Reward received by Market: $rewardEvent")

        rewards.firstOrNull { it.id == rewardEvent.rewardId }?.let { reward ->
            stepsExecutor.execute(
                steps = reward.steps,
                params = StepParams.fromReward(rewardEvent)
            )
        }
    }
}
