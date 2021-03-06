package fr.o80.twitckbot.extensions.overlay.layer.events

import fr.o80.twitckbot.extensions.overlay.model.LwjglEvent
import java.util.*

class EventsHolder(
    private val maxSize: Int
) {

    private val _events = LinkedList<LwjglEvent>()

    val events: List<LwjglEvent> get() = _events.toList()

    fun record(event: LwjglEvent) {
        if (_events.size == maxSize)
            _events.pollFirst()
        _events.offerLast(event)
    }
}
