package fr.o80.twitckbot.extensions.overlay

import fr.o80.twitckbot.extensions.overlay.graphics.ext.Vertex3f
import fr.o80.twitckbot.system.bean.Color

fun Color.toVertex3f(): Vertex3f {
    return Vertex3f(
        red / 255f,
        green / 255f,
        blue / 255f
    )
}
