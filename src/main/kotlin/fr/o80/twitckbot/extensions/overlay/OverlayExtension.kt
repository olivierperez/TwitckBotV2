package fr.o80.twitckbot.extensions.overlay

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.extensions.overlay.graphics.OverlayWindow
import fr.o80.twitckbot.extensions.overlay.graphics.ext.Vertex3f
import fr.o80.twitckbot.extensions.overlay.layer.InformativeLayer
import fr.o80.twitckbot.extensions.overlay.layer.PopupImageLayer
import fr.o80.twitckbot.extensions.overlay.layer.emotes.EmoteImageFactory
import fr.o80.twitckbot.extensions.overlay.layer.emotes.EmotesLayer
import fr.o80.twitckbot.extensions.overlay.layer.events.EventsHolder
import fr.o80.twitckbot.extensions.overlay.layer.events.EventsLayer
import fr.o80.twitckbot.extensions.overlay.model.LwjglEvent
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.overlay.Overlay
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.system.Extension
import fr.o80.twitckbot.system.event.EmotesEvent
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.OverlayEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@SessionScope
class OverlayExtension @Inject constructor(
    private val storage: Storage,
    loggerFactory: LoggerFactory,
    eventBus: EventBus,
) : Extension(), Overlay {

    private val logger = loggerFactory.getLogger(OverlayExtension::class.java.simpleName)

    private val width = 1920
    private val height = 1080
    private val greenBackgroundColor = Vertex3f(0f, 0.6f, 0f)
    private val textBackgroundColor: Vertex3f
    private val borderColor: Vertex3f
    private val textColor: Vertex3f

    private val window: OverlayWindow

    private val informativeLayer: InformativeLayer?

    private val eventsHolder = EventsHolder(8)
    private val eventsLayer: EventsLayer?

    private val popupImageLayer: PopupImageLayer

    private val emotesLayer: EmotesLayer?

    private val config: OverlayConfiguration

    init {
        logger.info("Initializing")

        config = readConfig("overlay.json")

        textBackgroundColor = config.style.backgroundColor.toVertex3f()
        borderColor = config.style.borderColor.toVertex3f()
        textColor = config.style.textColor.toVertex3f()

        val informativeText = config.informativeText
        informativeLayer = informativeText?.let {
            InformativeLayer(
                height = height,
                width = width,
                backgroundColor = textBackgroundColor,
                borderColor = borderColor,
                textColor = textColor,
                anchor = informativeText.anchor
            )
        }?.apply {
            setText(informativeText.text)
        }

        eventsLayer = config.events?.let {
            EventsLayer(
                config.style,
                it
            )
        }

        popupImageLayer = PopupImageLayer(
            height = height,
            width = width,
            backgroundColor = textBackgroundColor,
            borderColor = borderColor,
            textColor = textColor
        )

        emotesLayer = config.emotes?.takeIf { it.show }?.let {
            EmotesLayer(
                height = height,
                width = width,
                imageFactory = EmoteImageFactory(storage, logger),
                config = it
            )
        }

        window = OverlayWindow(
            title = "Streaming Overlay",
            width = width,
            height = height,
            bgColor = greenBackgroundColor,
            updatesPerSecond = 55,
            logger
        )

        if (config.enabled) {
            start()

            scope.launch {
                eventBus.events.filterIsInstance<EmotesEvent>().collect { event ->
                    onEmotesEvent(event)
                }
            }
        }
    }

    override fun alert(text: String, duration: Duration) {
        config.enabled || return
        informativeLayer?.popAlert(text, duration)
    }

    override fun onEvent(event: OverlayEvent) {
        config.enabled || return
        config.events?.let {
            eventsHolder.record(event.toLwjglEvent(it.secondsToLeave))
            eventsLayer?.update(eventsHolder.events)
        }
    }

    override fun showImage(path: String, duration: Duration) {
        config.enabled || return
        val imageStream = getImageStream(path)
        popupImageLayer.setImage(imageStream, null, duration)
    }

    override fun showImage(path: String, text: String, duration: Duration) {
        config.enabled || return
        val imageStream = getImageStream(path)
        popupImageLayer.setImage(imageStream, text, duration)
    }

    private fun onEmotesEvent(emotesEvent: EmotesEvent) {
        emotesLayer?.fountain(emotesEvent.emotes)
    }

    private fun getImageStream(path: String): InputStream {
        val imageFile = File(path)
        return if (imageFile.isFile && imageFile.canRead()) {
            imageFile.inputStream()
        } else {
            OverlayExtension::class.java.classLoader.getResourceAsStream(path)
                ?: throw IllegalArgumentException("Failed to load image for resources: $path")
        }
    }

    private fun start() {
        Thread(window).start()
        eventsLayer?.let { window.registerRender(it) }
        informativeLayer?.let { window.registerRender(it) }
        window.registerRender(popupImageLayer)
        emotesLayer?.let { window.registerRender(emotesLayer) }
    }

    private fun OverlayEvent.toLwjglEvent(secondsToLeave: Long): LwjglEvent {
        return LwjglEvent(
            this.text,
            Instant.now(),
            Duration.ofSeconds(secondsToLeave)
        )
    }

}
