package fr.o80.twitckbot.extensions.actions.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteAction(
    val name: String,
    val image: String,
    val execute: String
)
