package fr.o80.twitckbot.service.config

import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.service.oauth.AuthStorage
import fr.o80.twitckbot.service.storage.Storage
import java.time.Instant
import javax.inject.Inject

class LoadConfig @Inject constructor(
    private val authStorage: AuthStorage,
    private val storage: Storage
) {
    suspend operator fun invoke(): Config {
        val fullAuth = authStorage.readAuth()?.takeIf {
            it.botAuth.expiresAt.isAfter(Instant.now()) &&
                it.broadcasterAuth.expiresAt.isAfter(Instant.now())
        }

        val broadcasterName = storage.getGlobalInfo(NAMESPACE_CONFIG, KEY_BROADCASTER_NAME)

        return Config(
            broadcasterName = broadcasterName,
            auth = fullAuth
        )
    }
}
