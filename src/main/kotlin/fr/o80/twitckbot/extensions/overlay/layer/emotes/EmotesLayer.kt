package fr.o80.twitckbot.extensions.overlay.layer.emotes

import fr.o80.twitckbot.extensions.overlay.EmotesConfiguration
import fr.o80.twitckbot.extensions.overlay.graphics.Layer
import fr.o80.twitckbot.extensions.overlay.graphics.ext.PIF
import fr.o80.twitckbot.extensions.overlay.graphics.ext.Vertex2f
import fr.o80.twitckbot.extensions.overlay.graphics.ext.draw
import fr.o80.twitckbot.extensions.overlay.graphics.renderer.ImageRenderer
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.math.roundToInt
import kotlin.random.Random

private val GRAVITY = Vertex2f(0f, 0.5f)
private const val MAX_SPEED: Float = 30f
private const val MAX_INITIAL_ANGLE: Float = 10f
private const val MAX_FREEZE_TICKS: Int = 20
private const val INITIAL_SPEED_VARIATION: Float = 0.1f

class EmotesLayer(
    private val height: Int,
    width: Int,
    private val config: EmotesConfiguration,
    private val imageFactory: EmoteImageFactory,
    private val imageRenderer: ImageRenderer = ImageRenderer()
) : Layer {

    private val emotes = ConcurrentLinkedDeque<EmoteEntity>()

    private val startX = width / 2f
    private val startY = height / 1f

    override fun init() {
    }

    override fun tick() {
        emotes.forEach { emoteEntity ->
            emoteEntity.apply(GRAVITY, MAX_SPEED)
        }
        emotes.removeIf {
            it.position.y > height * 1.5f
        }
    }

    override fun render() {
        draw {
            pointSize(20f)
            color(0f, 0f, 0f)
            emotes.forEach { emoteEntity ->
                imageFactory.getImage(emoteEntity.code)?.let { image ->
                    imageRenderer.render(
                        image = image,
                        centeredPosition = emoteEntity.position
                    )
                }
            }
        }
    }

    fun fountain(emotes: List<String>) {
        this.emotes.addAll(
            emotes.flatMap { emoteInfo ->
                val split = emoteInfo.split(':')
                val emoteCode = split[0]
                val emoteCount =
                    (config.multiplier * (split[1].count { it == ',' } + 1)).roundToInt()
                (1..emoteCount).map { emoteCode }
            }.map { emoteCode ->
                EmoteEntity.of(
                    emoteCode,
                    startX,
                    startY,
                    (Random.nextFloat() - .5f) * 2f * (MAX_INITIAL_ANGLE * PIF / 360),
                    MAX_SPEED - (Random.nextFloat() * MAX_SPEED * INITIAL_SPEED_VARIATION),
                    Random.nextInt(MAX_FREEZE_TICKS)
                )
            }
        )
    }
}
