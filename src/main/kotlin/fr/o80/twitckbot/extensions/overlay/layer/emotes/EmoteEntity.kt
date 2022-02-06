package fr.o80.twitckbot.extensions.overlay.layer.emotes

import fr.o80.twitckbot.extensions.overlay.graphics.ext.Vertex2f
import fr.o80.twitckbot.extensions.overlay.graphics.ext.coerceAtMost
import kotlin.math.cos
import kotlin.math.sin

class EmoteEntity private constructor(
    val code: String,
    var position: Vertex2f,
    private var velocity: Vertex2f,
    private var freezeTime: Int
) {
    fun apply(gravity: Vertex2f, maxSpeed: Float) {
        if (freezeTime > 0) {
            freezeTime--
        } else {
            velocity = (velocity + gravity).coerceAtMost(maxSpeed)
            position += velocity
        }
    }

    companion object {
        fun of(
            code: String,
            x: Float,
            y: Float,
            angle: Float,
            speed: Float,
            freezeTime: Int
        ): EmoteEntity {
            return EmoteEntity(
                code,
                Vertex2f(x, y),
                Vertex2f(sin(angle) * -speed, cos(angle) * -speed),
                freezeTime
            )
        }
    }
}
