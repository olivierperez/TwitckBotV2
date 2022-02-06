package fr.o80.twitckbot.extensions.channel

import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.system.event.BitsEvent
import fr.o80.twitckbot.system.step.ActionStep
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor

class ChannelBits(
    private val steps: List<ActionStep>,
    private val stepsExecutor: StepsExecutor,
    private val logger: Logger
) {

    suspend fun interceptBitsEvent(bitsEvent: BitsEvent) {
        logger.debug("Bits intercepted in Channel extension: $bitsEvent")
        stepsExecutor.execute(
            steps = steps,
            params = StepParams.fromBits(bitsEvent)
        )
    }
}
