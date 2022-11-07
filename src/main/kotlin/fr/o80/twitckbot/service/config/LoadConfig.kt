package fr.o80.twitckbot.service.config

import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.service.oauth.AuthStorage
import java.time.Instant
import javax.inject.Inject

class LoadConfig @Inject constructor(
    private val authStorage: AuthStorage
) {
    operator fun invoke(): Config {
        val fullAuth = authStorage.readAuth()?.takeIf {
            it.botAuth.expiresAt.isAfter(Instant.now()) &&
                it.broadcasterAuth.expiresAt.isAfter(Instant.now())
        }

        return Config(
            broadcasterName = null,
            auth = fullAuth
        )
    }
}
