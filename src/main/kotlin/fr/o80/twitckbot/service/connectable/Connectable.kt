package fr.o80.twitckbot.service.connectable

import kotlinx.coroutines.flow.Flow

interface Connectable {
    val name: String
    val icon: String
    val status: Flow<ConnectableStatus>
    suspend fun connect()
}

enum class ConnectableStatus {
    NOT_CONNECTED,
    CONNECTED,
    CONNECTING
}
