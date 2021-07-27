package fr.o80.twitckbot.system.step

/*interface StepsExecutor {
    fun execute(
        steps: List<ActionStep>,
        params: StepParams
    )
}

class StepParams(
    val channel: String,
    val viewerName: String,
    val params: List<String> = emptyList(),
    val bits: Int? = null,
    val message: String? = null
) {
    companion object {
        fun fromCommand(commandEvent: CommandEvent, skipOptions: Int = 0): StepParams {
            return StepParams(
                channel = commandEvent.channel,
                viewerName = commandEvent.viewer.displayName,
                params = commandEvent.command.options.drop(skipOptions)
            )
        }

        fun fromBits(bitsEvent: BitsEvent): StepParams {
            return StepParams(
                channel = bitsEvent.channel,
                viewerName = bitsEvent.viewer.displayName,
                bits = bitsEvent.bits
            )
        }

        fun fromReward(rewardEvent: RewardEvent) :StepParams {
            return StepParams(
                channel = rewardEvent.channel,
                viewerName = rewardEvent.viewer.displayName,
                message = rewardEvent.message
            )
        }
    }
}*/
