package fr.o80.twitckbot.extensions.overlay.graphics.ext

import kotlin.math.absoluteValue

fun Vertex2f.coerceAtMost(maxSpeed: Float): Vertex2f {
    val ratio = size / maxSpeed
    return if (ratio.absoluteValue > 1) {
        Vertex2f(x / ratio, y / ratio)
    } else {
        this
    }
}
