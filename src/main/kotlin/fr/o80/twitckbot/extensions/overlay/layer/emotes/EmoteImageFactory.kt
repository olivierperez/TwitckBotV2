package fr.o80.twitckbot.extensions.overlay.layer.emotes

import fr.o80.twitckbot.extensions.overlay.graphics.model.Image
import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.service.storage.Storage

class EmoteImageFactory(
    private val storage: Storage,
    private val logger: Logger
) {

    private val cache: MutableMap<String, Image> = mutableMapOf()

    fun getImage(code: String): Image? {
        return try {
            cache.getOrPut(code) {
                val imageFile = storage.getPathOf("emotes", "$code.png")
                Image(imageFile.inputStream()).apply { load() }
            }
        } catch (e: Exception) {
            logger.error("Failed to load an image", e)
            null
        }
    }
}
