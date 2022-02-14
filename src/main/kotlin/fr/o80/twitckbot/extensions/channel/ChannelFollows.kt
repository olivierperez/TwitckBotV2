package fr.o80.twitckbot.extensions.channel

import fr.o80.twitckbot.system.bean.Follower
import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.event.FollowsEvent
import fr.o80.twitckbot.system.step.ActionStep
import fr.o80.twitckbot.system.step.StepParams
import fr.o80.twitckbot.system.step.StepsExecutor

class ChannelFollows(
    private val steps: List<ActionStep>,
    private val stepsExecutor: StepsExecutor
) {

    suspend fun interceptFollowEvent(followsEvent: FollowsEvent) {
        followsEvent.followers.data.forEach { newFollower ->
            val param = StepParams(
                "#${newFollower.toName.lowercase()}",
                createViewerFromName(newFollower)
            )
            stepsExecutor.execute(steps, param)
        }
    }

    private fun createViewerFromName(follower: Follower) =
        Viewer(
            login = follower.fromName,
            displayName = follower.fromName,
            badges = listOf(),
            userId = follower.fromId,
            color = "#FFFFFF"
        )
}
