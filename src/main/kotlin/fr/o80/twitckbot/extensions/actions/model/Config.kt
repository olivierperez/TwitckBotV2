package fr.o80.twitckbot.extensions.actions.model

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val actions: List<RemoteAction>,
    val scenes: List<Scene>
)
