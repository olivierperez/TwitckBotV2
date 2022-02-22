package fr.o80.twitckbot.extensions.actions.model

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val currentSceneId: String
)
