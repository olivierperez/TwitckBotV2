package fr.o80.twitckbot.extensions.channel

import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.step.ActionStep
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor

class ChannelCommands(
    private val commandConfigs: Map<String, List<ActionStep>>,
    private val stepsExecutor: StepsExecutor
) {

    suspend fun interceptCommandEvent(commandEvent: CommandEvent) {
        commandConfigs[commandEvent.command.tag]?.let { commandConfig ->
            stepsExecutor.execute(
                commandConfig,
                StepParams.fromCommand(commandEvent)
            )
        }
    }
}
