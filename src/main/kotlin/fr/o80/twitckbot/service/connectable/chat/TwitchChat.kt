package fr.o80.twitckbot.service.connectable.chat

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.connectable.Connectable
import fr.o80.twitckbot.service.connectable.ConnectableStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@SessionScope
class TwitchChat @Inject constructor(
    private val ircClient: IrcClient
) : Connectable {

    override val name: String
        get() = "Chat"

    override val icon: String
        get() = "ic_bot.png"

    private val _state = MutableStateFlow(ConnectableStatus.NOT_CONNECTED)
    override val status: Flow<ConnectableStatus> = _state

    init {
        ircClient.register(
            onConnectCallback = {
                _state.value = ConnectableStatus.CONNECTED
            },
            onDisconnectCallback = {
                _state.value = ConnectableStatus.NOT_CONNECTED
            }
        )
    }

    override suspend fun connect() {
        _state.value = ConnectableStatus.CONNECTING
        ircClient.connect()
    }
}
