package fr.o80.twitckbot.service.twitch

import fr.o80.twitckbot.system.bean.Channel
import fr.o80.twitckbot.system.bean.Follower
import fr.o80.twitckbot.system.bean.User
import fr.o80.twitckbot.system.bean.ValidateResponse
import fr.o80.twitckbot.system.bean.Video
import java.io.OutputStream

interface TwitchApi {

    fun getFollowers(streamId: String): List<Follower>

    fun getUser(userName: String): User

    fun getChannel(broadcasterId: String): Channel

    fun getVideos(userId: String, limit: Int): List<Video>

    fun downloadEmote(emoteId: String, outputStream: OutputStream)

    fun subscribeTo(
        topic: String,
        callbackUrl: String,
        leaseSeconds: Long,
        secret: String
    ): String

    fun unsubscribeFrom(
        topic: String,
        callbackUrl: String,
        leaseSeconds: Long,
        secret: String
    ): String

    fun validate(): ValidateResponse
}