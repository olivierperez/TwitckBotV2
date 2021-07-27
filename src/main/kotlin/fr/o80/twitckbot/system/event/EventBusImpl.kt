package fr.o80.twitckbot.system.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBusImpl @Inject constructor() : EventBus {

    private val _events = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val events: Flow<Event>
        get() = _events

    override suspend fun send(event: Event) {
        _events.emit(event)
    }
}
