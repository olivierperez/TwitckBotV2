package fr.o80.twitckbot.system.bean

import fr.o80.twitckbot.system.json.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Follower(
    val user: User
)

@Serializable
data class User(
    @SerialName("_id")
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val name: String,
    val logo: String
)

data class Channel(
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val game: String?,
    val followers: Int,
    val views: Int,
    val status: String?,
    val url: String,
    val logo: String,
    @SerialName("video_banner")
    val videoBanner: String
)

@Serializable
data class Video(
    val id: String,
    val title: String,
    val description: String?,
    val game: String,
    val url: String,
    @Serializable(with = DateSerializer::class)
    @SerialName("published_at")
    val publishedAt: Date
)

data class ValidateResponse(
    @SerialName("client_id")
    val clientId: String,
    @SerialName("user_id")
    val userId: String,
    val login: String,
    val scopes: List<String>
)

data class NewFollowers(
    val data: List<NewFollower>
)

data class NewFollower(
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
