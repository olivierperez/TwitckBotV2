package fr.o80.twitckbot.system.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ActionStep {
    enum class Type(val value: String) {
        COMMAND("command"),
        MESSAGE("message"),
        OVERLAY_EVENT("overlay_event"),
        OVERLAY_POPUP("overlay_popup"),
        SOUND("sound")
    }
}

@Serializable
@SerialName("command")
internal class CommandStep(
    val command: String
) : ActionStep()

@Serializable
@SerialName("message")
internal class MessageStep(
    val message: String
) : ActionStep()

@Serializable
@SerialName("overlay_event")
internal class OverlayEventStep(
    val text: String
) : ActionStep()

@Serializable
@SerialName("overlay_popup")
internal class OverlayPopupStep(
    val image: String,
    val text: String,
    val seconds: Long = 5
) : ActionStep()

@Serializable
@SerialName("sound")
internal class SoundStep(
    val soundId: String
) : ActionStep()
