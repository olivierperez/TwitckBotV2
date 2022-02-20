package fr.o80.twitckbot.service.connectable.chat

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.internal.time.CoolDownManager
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import fr.o80.twitckbot.system.event.SendWhisperEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Inject

private const val COOL_DOWN_NAMESPACE = "EventBusMessenger"

@SessionScope
class EventBusMessenger @Inject constructor(
    private val eventBus: EventBus,
    private val coolDownManager: CoolDownManager,
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var ircMessenger: IrcMessenger? = null

    private val messagesToSend: PriorityBlockingQueue<PostponedMessage> =
        PriorityBlockingQueue(11, PostponedMessageComparator)

    private var interrupted: Boolean = false

    private val intervalBetweenPostponed: Duration = Duration.ofSeconds(30)

    init {
        scope.launch { collectSendMessageEvent() }
        scope.launch { collectSendWhisperEvent() }
        scope.launch { sendQueuedMessages() }
    }

    fun start(messenger: IrcMessenger) {
        this.ircMessenger = messenger
    }

    fun interrupt() {
        interrupted = true
    }

    private suspend fun collectSendMessageEvent() {
        eventBus.events
            .filterIsInstance<SendMessageEvent>()
            .collect { event ->
                coolDownManager.executeIfCooledDown(
                    COOL_DOWN_NAMESPACE,
                    event.message,
                    event.coolDown
                ) {
                    if (event.priority == Priority.IMMEDIATE) {
                        ircMessenger?.send(event.channel, event.message)
                    } else {
                        messagesToSend.offer(event.toPostponedMessage())
                    }
                }
            }
    }

    private suspend fun collectSendWhisperEvent() {
        eventBus.events
            .filterIsInstance<SendWhisperEvent>()
            .collect { event ->
                ircMessenger?.send(event.channel, "/w ${event.recipient} ${event.message}")
            }
    }

    private suspend fun sendQueuedMessages() {
        while (!interrupted) {
            val message = withContext(Dispatchers.IO) { messagesToSend.take() }
            ircMessenger?.send(message.channel, message.content)
            delay(intervalBetweenPostponed)
        }
    }
}

private fun SendMessageEvent.toPostponedMessage(): PostponedMessage {
    return PostponedMessage(
        this.channel,
        this.message,
        this.priority
    )
}
