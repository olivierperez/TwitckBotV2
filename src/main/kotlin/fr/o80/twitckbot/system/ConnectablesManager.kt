package fr.o80.twitckbot.system

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.connectable.Connectable
import fr.o80.twitckbot.service.connectable.ConnectableStatus
import fr.o80.twitckbot.service.connectable.chat.TwitchChat
import fr.o80.twitckbot.service.connectable.slobs.SlobsConnectable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@SessionScope
class ConnectablesManager @Inject constructor(
    obsStudio: SlobsConnectable,
    twitchChat: TwitchChat
) {

    val connectables: List<Connectable> = listOf(
        twitchChat,
        obsStudio
    )

    suspend fun connect(name: String) {
        connectables.firstOrNull { it.name == name }?.connect()
    }

    fun statuses(): Flow<Pair<Connectable, ConnectableStatus>> {
        return connectables
            .map { connectable ->
                connectable.status.map { Pair(connectable, it) }
            }
            .merge()
    }
}
