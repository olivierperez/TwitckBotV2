package fr.o80.twitckbot.system.step

import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.event.BitsEvent
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.RewardEvent

interface StepsExecutor {
    suspend fun execute(
        steps: List<ActionStep>,
        params: StepParams
    )
}

class StepParams(
    val channel: String,
    val viewer: Viewer,
    val params: List<String> = emptyList(),
    val bits: Int? = null,
    val message: String? = null
) {
    companion object {
        fun fromCommand(commandEvent: CommandEvent, skipOptions: Int = 0): StepParams {
            return StepParams(
                channel = commandEvent.channel,
                viewer = commandEvent.viewer,
                params = commandEvent.command.options.drop(skipOptions)
            )
        }

        fun fromBits(bitsEvent: BitsEvent): StepParams {
            return StepParams(
                channel = bitsEvent.channel,
                viewer = bitsEvent.viewer,
                bits = bitsEvent.bits
            )
        }

        fun fromReward(rewardEvent: RewardEvent) :StepParams {
            return StepParams(
                channel = rewardEvent.channel,
                viewer = rewardEvent.viewer,
                message = rewardEvent.message
            )
        }
    }
}
