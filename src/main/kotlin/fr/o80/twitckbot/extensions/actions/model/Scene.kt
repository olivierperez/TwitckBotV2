package fr.o80.twitckbot.extensions.actions.model

import kotlinx.serialization.Serializable

@Serializable
data class Scene(
    val id: String,
    val name: String
)
