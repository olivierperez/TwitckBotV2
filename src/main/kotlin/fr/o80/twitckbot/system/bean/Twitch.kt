package fr.o80.twitckbot.system.bean

import fr.o80.twitckbot.system.json.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(
    @SerialName("_id")
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val name: String,
    val logo: String
)

@Serializable
data class Channel(
    @SerialName("broadcaster_id")
    val id: String,
    @SerialName("broadcaster_login")
    val login: String,
    @SerialName("broadcaster_name")
    val displayName: String,
    @SerialName("broadcaster_language")
    val language: String,
    @SerialName("game_name")
    val game: String?,
    @SerialName("game_id")
    val gameId: String?,
    val title: String,
    val delay: Int,
)

@Serializable
data class Video(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    @Serializable(with = DateSerializer::class)
    @SerialName("published_at")
    val publishedAt: LocalDateTime
)

@Serializable
data class ValidateResponse(
    @SerialName("client_id")
    val clientId: String,
    val scopes: List<String>
)

data class NewFollowers(
    val data: List<Follower>
)

@Serializable
data class Follower(
    @SerialName("from_id")
    val fromId: String,
    @SerialName("from_name")
    val fromName: String,
    @SerialName("to_id")
    val toId: String,
    @SerialName("to_name")
    val toName: String,
    @SerialName("followed_at")
    val followedAt: String
)

data class StreamsChanged(
    val data: List<StreamChanges>
)

data class StreamChanges(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("user_name")
    val userName: String,
    val title: String,
    val type: String
)
