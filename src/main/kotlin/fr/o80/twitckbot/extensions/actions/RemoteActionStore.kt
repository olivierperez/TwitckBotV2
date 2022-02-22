package fr.o80.twitckbot.extensions.actions

import fr.o80.twitckbot.extensions.actions.model.RemoteAction
import fr.o80.twitckbot.service.storage.Storage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val KEY_ACTIONS = "actions"

private val actionSerializer = Json

private inline fun <reified T : Any> String.parse(): T {
    return actionSerializer.decodeFromString<T>(this)
}

class RemoteActionStore @Inject constructor(
    private val storage: Storage
) {

    private val namespace: String = RemoteActionStore::class.java.name

    suspend fun getActions(): List<RemoteAction> {
        return storage.getGlobalInfo(namespace)
            .firstOrNull { it.first == KEY_ACTIONS }?.second
            ?.parse()
            ?: listOf()
    }

    suspend fun addAction(action: RemoteAction) {
        val actions = storage.getGlobalInfo(namespace)
            .firstOrNull { it.first == KEY_ACTIONS }?.second
            ?.parse<MutableList<RemoteAction>>()
            ?: mutableListOf()

        actions += action

        storage.putGlobalInfo(namespace, KEY_ACTIONS, actionSerializer.encodeToString(actions))
    }
}
