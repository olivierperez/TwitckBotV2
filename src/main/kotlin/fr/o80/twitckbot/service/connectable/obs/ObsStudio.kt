package fr.o80.twitckbot.service.connectable.obs

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.connectable.Connectable
import fr.o80.twitckbot.service.connectable.ConnectableStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@SessionScope
class ObsStudio @Inject constructor() : Connectable {

    private var attempt = 0

    override val name: String
        get() = "OBS"

    override val icon: String
        get() = "ic_stream.png"

    private val _state = MutableStateFlow(ConnectableStatus.NOT_CONNECTED)
    override val status: Flow<ConnectableStatus> = _state

    override suspend fun connect() {
        _state.value = ConnectableStatus.CONNECTING
        delay(500)
        attempt++

        if (attempt >= 2) {
            _state.value = ConnectableStatus.CONNECTED
        } else {
            _state.value = ConnectableStatus.NOT_CONNECTED
        }
    }
}
