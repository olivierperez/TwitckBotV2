package fr.o80.twitckbot.system.bean

import fr.o80.twitckbot.system.json.ColorSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ColorSerializer::class)
class Color(
    val red: Int,
    val green: Int,
    val blue: Int
)
