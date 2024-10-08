package fr.o80.twitckbot.internal.twitch

import fr.o80.twitckbot.data.model.Auth
import fr.o80.twitckbot.di.BroadcasterAuth
import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.twitch.TwitchApi
import fr.o80.twitckbot.system.bean.Channel
import fr.o80.twitckbot.system.bean.Follower
import fr.o80.twitckbot.system.bean.User
import fr.o80.twitckbot.system.bean.ValidateResponse
import fr.o80.twitckbot.system.bean.Video
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.OutputStream
import java.util.*
import javax.inject.Inject

private val twitchDeserializer = Json { ignoreUnknownKeys = true }

@SessionScope
class TwitchApiImpl @Inject constructor(
    @BroadcasterAuth
    private val auth: Auth,
    loggerFactory: LoggerFactory
) : TwitchApi {

    private var clientId: String? = null

    private val logger = loggerFactory.getLogger("NETWORK")

    private val client: HttpClient = HttpClients.createDefault()

    private val baseUrl: String = "https://api.twitch.tv/helix"

    init {
        validate()
    }

    override fun getFollowers(streamId: String): List<Follower> {
        var cursor: String? = null
        val followers = mutableListOf<Follower>()
""
        do {
            val url =
                if (cursor != null) "/channels/followers?first=100&broadcaster_id=$streamId&after=$cursor"
                else "/channels/followers?first=100&broadcaster_id=$streamId"

            val answer = doRequest(url).parse<FollowAnswer>()
            followers.addAll(answer.follows)
            cursor = answer.pagination.cursor
        } while (cursor != null)

        return followers
    }

    override fun getUser(userName: String): User {
        val url = "/users?login=$userName"
        val answer = doRequest(url).parse<UserAnswer>()
        return answer.users[0]
    }

    override fun getChannel(broadcasterId: String): Channel {
        val url = "/channels?broadcaster_id=$broadcasterId"
        return doRequest(url).parse()
    }

    override fun getVideos(userId: String, limit: Int): List<Video> {
        val url = "/videos?sort=time&first=$limit&user_id=$userId"
        val videoAnswer = doRequest(url).parse<VideoAnswer>()
        return videoAnswer.videos
    }

    override fun downloadEmote(emoteId: String, outputStream: OutputStream) {
        val request = HttpGet("https://static-cdn.jtvnw.net/emoticons/v1/$emoteId/1.0")
        val response = client.execute(request)
        response.entity.writeTo(outputStream)
    }

    private fun doRequest(url: String): String {
        val request = HttpGet("$baseUrl$url").apply {
            addHeader("Authorization", "Bearer ${auth.accessToken}")
            addHeader("Accept", "application/vnd.twitchtv.v5+json")
            if (clientId != null) {
                addHeader("Client-ID", clientId)
            }
        }

        val response = client.execute(request)
        val entity = response.entity
        val body = EntityUtils.toString(entity)
        logger.debug("Response: $body")

        return body
    }

    override fun subscribeTo(
        topic: String,
        callbackUrl: String,
        leaseSeconds: Long,
        secret: String
    ): String {
        val clientId = clientId ?: throw IllegalStateException("Client not yet retrieved")

        val request = HttpPost("https://api.twitch.tv/helix/webhooks/hub")
            .apply {
                addHeader("Client-ID", clientId)
                addHeader("Authorization", "Bearer ${auth.accessToken}")
                addHeader("Content-Type", "application/json")
                val payload = buildTopicSubscriptionPayload(
                    callbackUrl,
                    topic,
                    leaseSeconds,
                    "subscribe",
                    secret
                )
                logger.trace("Subscription payload: $payload")
                entity = StringEntity(payload)
            }

        val response = client.execute(request)
        return EntityUtils.toString(response.entity)
    }

    override fun unsubscribeFrom(
        topic: String,
        callbackUrl: String,
        leaseSeconds: Long,
        secret: String
    ): String {
        val clientId = clientId ?: throw IllegalStateException("Client not yet retrieved")

        val request = HttpPost("https://api.twitch.tv/helix/webhooks/hub")
            .apply {
                addHeader("Client-ID", clientId)
                addHeader("Authorization", "Bearer ${auth.accessToken}")
                addHeader("Content-Type", "application/json")
                val payload = buildTopicSubscriptionPayload(
                    callbackUrl,
                    topic,
                    leaseSeconds,
                    "unsubscribe",
                    secret
                )
                logger.trace("Un-subscription payload: $payload")
                entity = StringEntity(payload)
            }

        val response = client.execute(request)
        return EntityUtils.toString(response.entity)
    }

    override fun validate(): ValidateResponse {
        val request = HttpGet("https://id.twitch.tv/oauth2/validate")
            .apply {
                addHeader("Authorization", "Bearer ${auth.accessToken}")
                addHeader("Content-Type", "application/json")
            }

        val response = client.execute(request)
        val body = EntityUtils.toString(response.entity)
        return body.parse<ValidateResponse>().also { validateResponse ->
            clientId = validateResponse.clientId
        }
    }

    private inline fun <reified T : Any> String.parse(): T {
        return twitchDeserializer.decodeFromString<T>(this)
    }

    private fun buildTopicSubscriptionPayload(
        callbackUrl: String,
        topic: String,
        leaseSeconds: Long,
        mode: String,
        secret: String
    ): String {
        return """
                {
                    "hub.callback": "$callbackUrl",
                    "hub.mode": "$mode",
                    "hub.topic": "$topic",
                    "hub.lease_seconds": $leaseSeconds,
                    "hub.secret": "$secret"
                }
            """.trimIndent()
    }
}

@Serializable
class FollowAnswer(
    @SerialName("data")
    val follows: List<Follower>,
    val pagination: Pagination
)

@Serializable
class UserAnswer(
    @SerialName("data")
    val users: List<User>
)

@Serializable
class VideoAnswer(
    @SerialName("data")
    val videos: List<Video>
)

@Serializable
class Pagination(
    val cursor: String? = null
)
