package fr.o80.twitckbot.extensions.overlay.layer.events

import fr.o80.twitckbot.extensions.overlay.EventsConfiguration
import fr.o80.twitckbot.extensions.overlay.OverlayStyle
import fr.o80.twitckbot.extensions.overlay.graphics.Layer
import fr.o80.twitckbot.extensions.overlay.graphics.ext.Draw
import fr.o80.twitckbot.extensions.overlay.graphics.ext.Vertex3f
import fr.o80.twitckbot.extensions.overlay.graphics.ext.draw
import fr.o80.twitckbot.extensions.overlay.graphics.renderer.TextRenderer
import fr.o80.twitckbot.extensions.overlay.model.LwjglEvent
import fr.o80.twitckbot.extensions.overlay.toVertex3f
import java.time.Instant

class EventsLayer(
    private val style: OverlayStyle,
    private val config: EventsConfiguration,
    private val textRenderer: TextRenderer = TextRenderer(
        "fonts/Roboto-Light.ttf",
        fontHeight = config.fontSize
    )
) : Layer {

    private var events: List<LwjglEvent> = emptyList()

    override fun init() {
        textRenderer.init()
    }

    override fun tick() {
        val now = Instant.now()
        events = events.filter { (it.since + it.duration).isAfter(now) }
    }

    override fun render() {
        draw {
            if (config.showFrame) drawFrame()
            drawEvents()
        }
    }

    private fun Draw.drawFrame() {
        color(style.borderColor.toVertex3f())
        rect(
            config.x,
            config.y,
            config.x + config.width,
            config.y + config.height
        )
    }

    fun update(events: List<LwjglEvent>) {
        this.events = events.asReversed()
    }

    private fun Draw.drawEvents() {
        pushed {
            translate(config.x, config.y + config.height - config.blockMargin, 0f)

            val blockHeight = config.fontSize + 10f
            val verticalSpacing = 5f

            val bgColor = style.backgroundColor.toVertex3f()
            val textColor = style.textColor.toVertex3f()

            events.forEach { overlayEvent ->
                translate(0f, -blockHeight, 0f)
                drawBackground(bgColor, blockHeight)
                drawText(textColor, overlayEvent)
                translate(0f, -verticalSpacing, 0f)
            }
        }
    }

    private fun Draw.drawBackground(
        bgColor: Vertex3f,
        blockHeight: Float
    ) {
        color(bgColor)
        quad(config.blockMargin, 0f, config.width - config.blockMargin, blockHeight)
    }

    private fun Draw.drawText(
        textColor: Vertex3f,
        overlayEvent: LwjglEvent
    ) {
        pushed {
            translate(config.blockMargin + 10f, 1f, 0f)
            color(textColor)
            textRenderer.render(overlayEvent.text)
        }
    }

}
