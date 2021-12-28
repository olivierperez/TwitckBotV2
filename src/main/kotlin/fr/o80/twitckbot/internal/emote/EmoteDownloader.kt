package fr.o80.twitckbot.internal.emote

import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.service.twitch.TwitchApi
import javax.inject.Inject

private const val EMOTES_DIR = "emotes"

class EmoteDownloader @Inject constructor(
    private val twitchApi: TwitchApi,
    private val storage: Storage,
    loggerFactory: LoggerFactory,
) {

    private val logger: Logger = loggerFactory.getLogger(EmoteDownloader::class)

    suspend fun download(emotes: Iterable<String>, andThen: suspend () -> Unit) {
        emotes.map { it.split(':')[0] }
            .forEach { emoteCode -> download(emoteCode) }
        andThen()
    }

    fun download(emoteCode: String) {
        val emoteFile = storage.getPathOf(EMOTES_DIR, "$emoteCode.png")
        if (emoteFile.exists()) {
            logger.debug("Emote $emoteCode already exists into $emoteFile")
        } else {
            logger.debug("Downloading emote: $emoteCode into $emoteFile")
            val outputStream = emoteFile.outputStream()
            twitchApi.downloadEmote(emoteCode, outputStream)
        }
    }
}
