package fr.o80.twitckbot.service.oauth

import fr.o80.twitckbot.service.oauth.model.AuthResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import javax.inject.Inject

class AuthenticateBroadcaster @Inject constructor() {

    private val json = Json

    operator fun invoke(clientId: String, clientSecret: String): AuthResponse {
        val client: HttpClient = HttpClients.createDefault()
        val scopes = listOf(
            "bits:read",
            "chat:read",
            "channel:moderate",
            "channel:read:hype_train",
            "channel:read:redemptions",
            "channel:read:subscriptions",
            "channel_subscriptions"
        )
        val post = HttpPost(
            "https://id.twitch.tv/oauth2/token" +
                "?client_id=$clientId" +
                "&client_secret=$clientSecret" +
                "&grant_type=client_credentials" +
                "&scope=${scopes.joinToString("%20")}"
        )

        val response = client.execute(post)
        val responseStr = EntityUtils.toString(response.entity)
        return json.decodeFromString(responseStr)
    }
}
