package fr.o80.twitckbot.data

import dagger.Reusable
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.service.oauth.AuthStorage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

@Reusable
class SettingsStorage @Inject constructor() : AuthStorage {

    private val serializer = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override fun store(
        fullAuth: FullAuth
    ) {
        File(".storage/auth.json")
            .bufferedWriter()
            .use { it.write(fullAuth.encode()) }
    }

    override fun readAuth(): FullAuth? {
        return File(".storage/auth.json")
            .takeIf { it.isFile }
            ?.bufferedReader()
            ?.use { it.readText().decode() }
    }

    private fun FullAuth.encode(): String {
        return serializer.encodeToString(this)
    }

    private inline fun <reified T> String.decode(): T {
        return serializer.decodeFromString(this)
    }

}
