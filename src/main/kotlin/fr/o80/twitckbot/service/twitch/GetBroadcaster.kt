package fr.o80.twitckbot.service.twitch

import fr.o80.twitckbot.di.BroadcasterName
import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.system.bean.Badge
import fr.o80.twitckbot.system.bean.Viewer
import javax.inject.Inject

@SessionScope
class GetBroadcaster @Inject constructor(
    private val twitchApi: TwitchApi,
    @BroadcasterName
    private val broadcasterName: String
) {
    operator fun invoke(): Viewer {
        val broadcasterId = twitchApi.getUser(broadcasterName).id
        return Viewer(
            login = broadcasterName,
            displayName = broadcasterName,
            badges = listOf(Badge.BROADCASTER),
            userId = broadcasterId,
            color = "#000000"
        )
    }
}
