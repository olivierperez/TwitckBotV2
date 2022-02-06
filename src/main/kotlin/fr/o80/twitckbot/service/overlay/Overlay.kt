package fr.o80.twitckbot.service.overlay

import fr.o80.twitckbot.system.event.OverlayEvent
import java.time.Duration

interface Overlay {
    fun alert(text: String, duration: Duration)
    fun onEvent(event: OverlayEvent)
    fun showImage(path: String, duration: Duration)
    fun showImage(path: String, text: String, duration: Duration)
}
