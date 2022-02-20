package fr.o80.twitckbot.service.connectable.chat

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import fr.o80.twitckbot.system.event.SendWhisperEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@SessionScope
class EventBusMessenger @Inject constructor(
    private val eventBus: EventBus,
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var ircMessenger: IrcMessenger? = null

    init {
        scope.launch {
            eventBus.events
                .filterIsInstance<SendMessageEvent>()
                .collect { event ->
                    ircMessenger?.send(event.channel, event.message)
                }
        }
        scope.launch {
            eventBus.events
                .filterIsInstance<SendWhisperEvent>()
                .collect { event ->
                    ircMessenger?.send(event.channel, "/w ${event.recipient} ${event.message}")
                }
        }
    }

    fun start(messenger: IrcMessenger) {
        this.ircMessenger = messenger
    }
}
