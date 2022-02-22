package fr.o80.twitckbot.service.twitch

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.system.bean.Badge
import fr.o80.twitckbot.system.bean.Viewer
import javax.inject.Inject

@SessionScope
class GetBroadcaster @Inject constructor(
    private val twitchApi: TwitchApi
) {

    // TODO OPZ
    private val hostname = "gnu_coding_cafe"

    operator fun invoke(): Viewer {
        val broadcasterId = twitchApi.getUser(hostname).id
        return Viewer(
            login = hostname,
            displayName = hostname,
            badges = listOf(Badge.BROADCASTER),
            userId = broadcasterId,
            color = "#000000"
        )
    }
}
