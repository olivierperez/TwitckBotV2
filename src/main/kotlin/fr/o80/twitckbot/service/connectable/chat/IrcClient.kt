package fr.o80.twitckbot.service.connectable.chat

import fr.o80.twitckbot.data.model.Auth
import fr.o80.twitckbot.di.BotAuth
import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.SendMessageEvent
import fr.o80.twitckbot.system.line.PrivMsgLineInterpreter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import org.jibble.pircbot.PircBot
import java.util.logging.Logger
import javax.inject.Inject

// Server constants
internal const val HOST = "irc.twitch.tv"
internal const val PORT = 6667

// Server messages
internal const val SERVER_MEMANS = ":tmi.twitch.tv CAP * ACK :twitch.tv/membership"
internal const val SERVER_MEMREQ = "CAP REQ :twitch.tv/membership"
internal const val SERVER_CMDREQ = "CAP REQ :twitch.tv/commands"
internal const val SERVER_CMDANS = ":tmi.twitch.tv CAP * ACK :twitch.tv/commands"
internal const val SERVER_TAGREG = "CAP REQ :twitch.tv/tags"
internal const val SERVER_TAGANS = ":tmi.twitch.tv CAP * ACK :twitch.tv/tags"

private typealias Callback = () -> Unit

@SessionScope
class IrcClient @Inject constructor(
    @BotAuth
    private val auth: Auth,
    private val privMsgLineInterpreter: PrivMsgLineInterpreter,
    private val eventBus: EventBus
) : PircBot(), IrcMessenger {

    private val ping = Ping(this)

    private val logger = Logger.getLogger("Chat Client")

    private val initializer = TwitchChatInitChecker()

    private var onConnectCallback: Callback? = null
    private var onDisconnectCallback: Callback? = null

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun connect() {
        try {
            logger.info("Attempting to connect to irc.twitch.tv...")

            scope.launch(Dispatchers.IO) {
                connect(HOST, PORT, "oauth:${auth.accessToken}")
            }.join()

            logger.info("Requesting twitch membership capability for NAMES/JOIN/PART/MODE messages...")
            sendRawLine(SERVER_MEMREQ)

            logger.info("Requesting twitch commands capability for NOTICE/HOSTTARGET/CLEARCHAT/USERSTATE messages... ")
            sendRawLine(SERVER_CMDREQ)

            logger.info("Requesting twitch tags capability for PRIVMSG/USERSTATE/GLOBALUSERSTATE messages... ")
            sendRawLine(SERVER_TAGREG)

            while (!initializer.initialized) {
                delay(1000)
                if (!initializer.initialized) logger.info("Not yet initialized")
            }

            logger.info("Chat initialized!")
            joinChannel("#gnu_coding_cafe") // TODO Join the right channel

            scope.launch {
                eventBus.events
                    .filterIsInstance<SendMessageEvent>()
                    .collect {
                        send(it.channel, it.message)
                    }
            }
        } catch (e: Exception) {
            logger.severe("Something gone wrong at startup: " + e.message)
            onDisconnectCallback?.invoke()
        }
    }

    override fun onConnect() {
        logger.info("onConnect")
        super.onConnect()
        onConnectCallback?.invoke()
    }

    override fun onDisconnect() {
        logger.warning("onDisconnect")
        super.onDisconnect()
        onDisconnectCallback?.invoke()
    }

    override fun handleLine(line: String?) {
        logger.info("Handle line: $line")
        super.handleLine(line)
        line ?: return

        scope.launch {
            initializer.handleLine(line)
            ping.handleLine(line)
            privMsgLineInterpreter.handle(line)
        }
    }

    override fun sendLine(message: String) {
        super.sendRawLine(message)
    }

    override fun send(channel: String, message: String) {
        super.sendMessage(channel, message)
        Logger.getLogger("debug").info("Messange sent to $channel: $message")
    }

    fun register(onConnectCallback: () -> Unit, onDisconnectCallback: () -> Unit) {
        this.onConnectCallback = onConnectCallback
        this.onDisconnectCallback = onDisconnectCallback
    }
}