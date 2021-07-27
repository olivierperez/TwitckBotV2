package fr.o80.twitckbot.internal.storage.bean

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val extras: MutableMap<String, String> = mutableMapOf()
) {

    fun putExtra(key: String, value: String) {
        extras[key] = value
    }

    fun getExtra(key: String): String? {
        return extras[key]
    }
}
