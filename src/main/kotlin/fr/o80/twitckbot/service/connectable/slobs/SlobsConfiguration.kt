package fr.o80.twitckbot.service.connectable.slobs

import kotlinx.serialization.Serializable

@Serializable
data class SlobsConfiguration(
    val host: String,
    val port: Int,
    val token: String,
)
