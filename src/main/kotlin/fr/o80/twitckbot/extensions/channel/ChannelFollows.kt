package fr.o80.twitckbot.extensions.channel

import fr.o80.twitckbot.system.bean.NewFollower
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

    private fun createViewerFromName(newFollower: NewFollower) =
        Viewer(
            login = newFollower.fromName,
            displayName = newFollower.fromName,
            badges = listOf(),
            userId = newFollower.fromId,
            color = "#FFFFFF"
        )
}
