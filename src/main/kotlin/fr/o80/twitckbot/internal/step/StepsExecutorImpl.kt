package fr.o80.twitckbot.internal.step

import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.overlay.Overlay
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.system.command.CommandParser
import fr.o80.twitckbot.system.event.CommandEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.MessageEvent
import fr.o80.twitckbot.system.event.OverlayEvent
import fr.o80.twitckbot.system.step.ActionStep
import fr.o80.twitckbot.system.step.CommandStep
import fr.o80.twitckbot.system.step.MessageStep
import fr.o80.twitckbot.system.step.OverlayEventStep
import fr.o80.twitckbot.system.step.OverlayPopupStep
import fr.o80.twitckbot.system.step.SoundStep
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor
import java.time.Duration
import javax.inject.Inject

class StepsExecutorImpl @Inject constructor(
    private val commandParser: CommandParser,
    private val stepFormatter: StepFormatter,
    private val eventBus: EventBus,
    private val overlay: Overlay?,
    private val sound: Sound?,
    loggerFactory: LoggerFactory
) : StepsExecutor {

    private val logger: Logger = loggerFactory.getLogger(StepsExecutor::class)

    override suspend fun execute(
        steps: List<ActionStep>,
        params: StepParams
    ) {
        steps.forEach { step ->
            when (step) {
                is CommandStep -> execute(step, params)
                is MessageStep -> send(step, params)
                is OverlayPopupStep -> showPopup(step, params)
                is OverlayEventStep -> showEvent(step, params)
                is SoundStep -> play(step)
            }
        }
    }

    private suspend fun execute(
        step: CommandStep,
        params: StepParams
    ) {
        val commandMessage = stepFormatter.format(step.command, params)
        val command = commandParser.parse(commandMessage)
            ?: throw IllegalArgumentException("Failed to convert ${step.command} to a valid command")
        eventBus.send(CommandEvent(params.channel, command, bits = null, viewer = params.viewer))
    }

    private suspend fun send(
        step: MessageStep,
        params: StepParams
    ) {
        val message = stepFormatter.format(step.message, params)
        eventBus.send(MessageEvent(params.channel, viewer = params.viewer, message))
    }

    private fun showPopup(
        step: OverlayPopupStep,
        params: StepParams
    ) {
        if (overlay == null) {
            logger.error("Overlay popup steps require Overlay extension to work")
        } else {
            val text = stepFormatter.format(step.text, params)
            overlay.showImage(step.image, text, Duration.ofSeconds(step.seconds))
        }
    }

    private fun showEvent(
        step: OverlayEventStep,
        params: StepParams
    ) {
        if (overlay == null) {
            logger.error("Overlay event steps require Overlay extension to work")
        } else {
            val text = stepFormatter.format(step.text, params)
            overlay.onEvent(OverlayEvent(text))
        }
    }

    private fun play(step: SoundStep) {
        if (sound == null) {
            logger.error("Sound steps require Sound extension to work")
        } else {
            sound.play(step.soundId)
        }
    }

}
