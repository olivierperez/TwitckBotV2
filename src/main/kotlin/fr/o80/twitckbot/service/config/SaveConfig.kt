package fr.o80.twitckbot.service.config

import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.service.oauth.AuthStorage
import fr.o80.twitckbot.service.storage.Storage
import javax.inject.Inject

const val NAMESPACE_CONFIG = "fr.o80.twitckbot.service.config"
const val KEY_BROADCASTER_NAME = "BroadcasterName"

class SaveConfig @Inject constructor(
    private val authStorage: AuthStorage,
    private val storage: Storage
) {
    suspend operator fun invoke(broadcasterName: String, fullAuth: FullAuth): Config {
        authStorage.store(fullAuth)
        storage.putGlobalInfo(NAMESPACE_CONFIG, KEY_BROADCASTER_NAME, broadcasterName)

        return Config(
            broadcasterName = null,
            auth = fullAuth
        )
    }
}
