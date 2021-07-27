package fr.o80.twitckbot.data.model

import fr.o80.twitckbot.data.serializer.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class FullAuth(
    val botAuth: Auth,
    val broadcasterAuth: Auth,
)

@Serializable
data class Auth(
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_at")
    @Serializable(with = InstantSerializer::class)
    val expiresAt: Instant,
    @SerialName("scope")
    val scopes: List<String>
)
