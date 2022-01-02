package fr.o80.twitckbot.extensions.sound

import kotlinx.serialization.Serializable

@Serializable
class SoundConfiguration(
    val enabled: Boolean,
    val celebration: OneSound,
    val negative: OneSound,
    val positive: OneSound,
    val raid: OneSound,
    val custom: Map<String, OneSound>
)

@Serializable
class OneSound(
    val path: String,
    val gain: Float
)
