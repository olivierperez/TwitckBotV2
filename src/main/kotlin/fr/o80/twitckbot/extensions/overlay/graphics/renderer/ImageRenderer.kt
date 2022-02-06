package fr.o80.twitckbot.extensions.overlay.graphics.renderer

import fr.o80.twitckbot.extensions.overlay.graphics.ext.Vertex2f
import fr.o80.twitckbot.extensions.overlay.graphics.ext.draw
import fr.o80.twitckbot.extensions.overlay.graphics.model.Image
import org.lwjgl.opengl.GL46

class ImageRenderer {

    fun render(image: Image, centeredPosition: Vertex2f) {
        draw {
            val left = centeredPosition.x - image.width / 2
            val right = left + image.width
            val top = centeredPosition.y - image.height / 2
            val bottom = top + image.height

            texture2d {
                GL46.glBindTexture(GL46.GL_TEXTURE_2D, image.id)
                color(1f, 1f, 1f)

                GL46.glBegin(GL46.GL_QUADS)
                GL46.glTexCoord2f(0f, 0f)
                GL46.glVertex2f(left, top)

                GL46.glTexCoord2f(1f, 0f)
                GL46.glVertex2f(right, top)

                GL46.glTexCoord2f(1f, 1f)
                GL46.glVertex2f(right, bottom)

                GL46.glTexCoord2f(0f, 1f)
                GL46.glVertex2f(left, bottom)
                GL46.glEnd()
            }
        }
    }
}
