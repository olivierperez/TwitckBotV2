package fr.o80.twitckbot.system.event

import kotlinx.coroutines.flow.Flow

interface EventBus {
    suspend fun send(event: Event)
    val events: Flow<Event>
}
