package fr.o80.twitckbot.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val broadcasterName: String?,
    val auth: FullAuth?
) {
    fun isComplete(): Boolean {
        return !broadcasterName.isNullOrBlank() && auth != null
    }
}
