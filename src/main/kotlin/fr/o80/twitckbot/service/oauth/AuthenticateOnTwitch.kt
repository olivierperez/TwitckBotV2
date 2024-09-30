package fr.o80.twitckbot.service.oauth

import fr.o80.twitckbot.data.model.Auth
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.javascriptHashRedirection
import fr.o80.twitckbot.oauthEndpoint
import fr.o80.twitckbot.oauthRedirectUri
import fr.o80.twitckbot.service.oauth.model.AuthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class AuthenticateOnTwitch @Inject constructor(
    private val authenticateBroadcaster: AuthenticateBroadcaster
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
                        val botAuth = this.capture()
                        val broadcasterAuth = authenticateBroadcaster(clientId, clientSecret)
                        val fullAuth = FullAuth(botAuth.toAuth(), broadcasterAuth.toAuth())

                        onAuthCompleted(fullAuth)
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

    private fun PipelineContext<Unit, ApplicationCall>.capture(): AuthResponse {
        val accessToken = context.request.queryParameters["access_token"]!!
        val scopes = context.request.queryParameters["scope"]!!
        val tokenType = context.request.queryParameters["token_type"]!!

        return AuthResponse(
            accessToken,
            tokenType,
            expiresIn = 31_536_000,
            scopes.split(' ')
        )
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
