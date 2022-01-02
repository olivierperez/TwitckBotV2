package fr.o80.twitckbot.extensions.sound

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.config.readConfig
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.system.Extension
import javax.inject.Inject

@SessionScope
class SoundExtension @Inject constructor(
    loggerFactory: LoggerFactory,
) : Sound, Extension() {

    private val logger = loggerFactory.getLogger(SoundExtension::class.java.simpleName)

    private val config: SoundConfiguration = readConfig("sound.json")

    private lateinit var soundPlayer: SoundPlayer

    init {
        logger.info("Initializing")

        val sounds = mutableMapOf<String, OneSound>().apply {
            config.custom.forEach { (id, oneSound) ->
                put(id, oneSound)
            }
            put("celebration", config.celebration)
            put("negative", config.negative)
            put("positive", config.positive)
            put("raid", config.raid)
        }

        soundPlayer = SoundPlayer(logger, sounds)
    }

    override fun play(id: String) {
        config.enabled || return
        soundPlayer.play(id)
    }

    override fun playCelebration() {
        config.enabled || return
        soundPlayer.play("celebration")
    }

    override fun playNegative() {
        config.enabled || return
        soundPlayer.play("negative")
    }

    override fun playPositive() {
        config.enabled || return
        soundPlayer.play("positive")
    }

    override fun playRaid() {
        config.enabled || return
        soundPlayer.play("raid")
    }
}
