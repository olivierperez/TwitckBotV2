package fr.o80.twitckbot.extensions.rewards

import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.service.time.TimeChecker
import fr.o80.twitckbot.system.bean.ChannelName
import fr.o80.twitckbot.system.bean.Viewer
import fr.o80.twitckbot.system.event.CommandEvent

class RewardsCommands(
    private val channel: ChannelName,
    private val claimConfig: RewardsClaim,
    private val i18n: RewardsI18n,
    private val claimTimeChecker: TimeChecker,
    private val points: Points,
//    TODO Overlay
//    private val overlay: OverlayExtension?,
    private val sound: Sound?
) {

    suspend fun interceptCommandEvent(commandEvent: CommandEvent): CommandEvent {
        if (channel.name != commandEvent.channel)
            return commandEvent

        when (commandEvent.command.tag) {
            claimConfig.command -> claim(commandEvent.viewer)
        }

        return commandEvent
    }

    private suspend fun claim(viewer: Viewer) {
        if (claimConfig.reward == 0) return

        claimTimeChecker.executeIfNotCooldown(viewer.login) {
            points.addPoints(viewer.login, claimConfig.reward)

            val message = i18n.viewerJustClaimed
                .replace("#USER#", viewer.displayName)
                .replace("#NEW_POINTS#", claimConfig.reward.toString())
                .replace("#OWNED_POINTS#", points.getPoints(viewer.login).toString())

            playCoin()
            displayCoinAndMessage(message)
        }.fallback {
            playFail()
        }
    }

    private fun playCoin() {
        sound?.play(claimConfig.positiveSound)
    }

    private fun playFail() {
        sound?.play(claimConfig.negativeSound)
    }

    private fun displayCoinAndMessage(message: String) {
//        TODO Overlay
//        overlay?.showImage(claimConfig.image, message, Duration.ofSeconds(5))
    }

}
