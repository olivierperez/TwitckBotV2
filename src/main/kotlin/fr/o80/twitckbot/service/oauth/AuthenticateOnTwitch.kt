package fr.o80.twitckbot.service.oauth

import fr.o80.twitckbot.data.model.Auth
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.javascriptHashRedirection
import fr.o80.twitckbot.oauthEndpoint
import fr.o80.twitckbot.oauthRedirectUri
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class AuthenticateOnTwitch @Inject constructor(
    private val authStorage: AuthStorage
) {

    operator fun invoke(
        port: Int,
        clientId: String,
        clientSecret: String,
        onAuthCompleted: (FullAuth) -> Unit,
        onAuthFailed: (Exception) -> Unit
    ): String {
        val atomicServer = AtomicReference<NettyApplicationEngine>()
        embeddedServer(Netty, port) {
            routing {
                get("oauth") {
                    redirectHash(port)
                }
                get("capture") {
                    try {
                        val authResponse = capture(clientId, clientSecret)
                        FullAuth(
                            authResponse.botAuthentication.toAuth(),
                            authResponse.broadcasterAuthentication.toAuth(),
                        ).let { fullAuth ->
                            authStorage.store(fullAuth)
                            onAuthCompleted(fullAuth)
                        }
                    } catch (e: Exception) {
                        onAuthFailed(e)
                    }
                    Thread {
                        atomicServer.get().stop(1, 5, TimeUnit.SECONDS)
                    }.start()
                }
            }
        }.also { server ->
            atomicServer.set(server)
            server.start(wait = false)

            val scope = listOf(
                "bits:read",
                "chat:read",
                "chat:edit",
                "channel:moderate",
                "channel:read:hype_train",
                "channel:read:redemptions",
                "channel:read:subscriptions",
                "channel_subscriptions"
            )

            return StringBuilder(oauthEndpoint)
                .append("?response_type=token")
                .append("&client_id=$clientId")
                .append("&redirect_uri=${URI(oauthRedirectUri.format(port)).toASCIIString()}")
                .append("&scope=${scope.joinToString("%20")}")
                .append("&state=${UUID.randomUUID()}")
                .toString()
        }
    }

    private suspend fun PipelineContext<Unit, ApplicationCall>.redirectHash(port: Int) {
        context.response.call.respondText(
            javascriptHashRedirection.format(port),
            ContentType.Text.Html
        )
    }

    private fun PipelineContext<Unit, ApplicationCall>.capture(
        clientId: String,
        clientSecret: String
    ): FullAuthentication {
        val accessToken = context.request.queryParameters["access_token"]!!
        val scopes = context.request.queryParameters["scope"]!!
        val tokenType = context.request.queryParameters["token_type"]!!

        return FullAuthentication(
            botAuthentication = AuthResponse(
                accessToken,
                tokenType,
                expiresIn = 31_536_000,
                scopes.split(' ')
            ),
            broadcasterAuthentication = auth(clientId, clientSecret)
        )
    }

    private fun auth(clientId: String, clientSecret: String): AuthResponse {
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
        return Json.decodeFromString<AuthResponse>(responseStr)
    }

}

private fun AuthResponse.toAuth(): Auth {
    return Auth(
        tokenType,
        accessToken,
        Instant.now() + Duration.ofSeconds(expiresIn),
        scopes
    )
}
