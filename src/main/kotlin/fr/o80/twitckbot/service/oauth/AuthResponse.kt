package fr.o80.twitckbot.service.oauth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullAuthentication(
    val botAuthentication: AuthResponse,
    val broadcasterAuthentication: AuthResponse,
)

@Serializable
data class AuthResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("scope")
    val scopes: List<String>
)
