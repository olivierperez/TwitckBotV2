package fr.o80.twitckbot.system.bean

import fr.o80.twitckbot.system.json.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Follower(
    val user: User
)

@Serializable
data class User(
    val id: String,
    val displayName: String,
    val name: String,
    val logo: String
)

data class Channel(
    val id: String,
    val displayName: String,
    val game: String?,
    val followers: Int,
    val views: Int,
    val status: String?,
    val url: String,
    val logo: String,
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
    val publishedAt: Date
)

data class ValidateResponse(
    val clientId: String,
    val userId: String,
    val login: String,
    val scopes: List<String>
)

data class NewFollowers(
    val data: List<NewFollower>
)

data class NewFollower(
    val fromId: String,
    val fromName: String,
    val toId: String,
    val toName: String,
    val followedAt: String
)

data class StreamsChanged(
    val data: List<StreamChanges>
)

data class StreamChanges(
    val id: String,
    val userId: String,
    val userName: String,
    val title: String,
    val type: String
)
