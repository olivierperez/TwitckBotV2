package fr.o80.twitckbot.extensions.overlay.layer.emotes

import fr.o80.twitckbot.extensions.overlay.graphics.model.Image
import fr.o80.twitckbot.service.storage.Storage

class EmoteImageFactory(
    private val storage: Storage
) {

    private val cache: MutableMap<String, Image> = mutableMapOf()

    fun getImage(code: String): Image {
        return cache.getOrPut(code) {
            val imageFile = storage.getPathOf("emotes", "$code.png")
            Image(imageFile.inputStream()).apply { load() }
        }
    }
}
