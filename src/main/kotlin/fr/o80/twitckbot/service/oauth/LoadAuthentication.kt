package fr.o80.twitckbot.service.oauth

import fr.o80.twitckbot.data.model.FullAuth
import java.time.Instant
import javax.inject.Inject

class LoadAuthentication @Inject constructor(
    private val authStorage: AuthStorage
) {
    operator fun invoke(): FullAuth? {
        return authStorage.readAuth()?.takeIf {
            it.botAuth.expiresAt.isAfter(Instant.now()) &&
                it.broadcasterAuth.expiresAt.isAfter(Instant.now())
        }
    }
}
