package fr.o80.twitckbot.extensions.overlay

import fr.o80.twitckbot.system.bean.Color
import kotlinx.serialization.Serializable

@Serializable
class OverlayConfiguration(
    val enabled: Boolean,
    val informativeText: InformativeText?,
    val events: EventsConfiguration?,
    val emotes: EmotesConfiguration?,
    val style: OverlayStyle
)

@Serializable
class OverlayStyle(
    val borderColor: Color,
    val backgroundColor: Color,
    val textColor: Color
)

@Serializable
class InformativeText(
    val text: String,
    val anchor: Anchor
)

@Serializable
class EmotesConfiguration(
    val show: Boolean,
    val multiplier: Float
)

@Serializable
class EventsConfiguration(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val fontSize: Float,
    val blockMargin: Float,
    val secondsToLeave: Long,
    val showFrame: Boolean
)

enum class Anchor {
    BottomLeft,
    BottomCenter,
    BottomRight
}
