package fr.o80.twitckbot.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import fr.o80.twitckbot.extensions.actions.RemoteActionsExtension
import fr.o80.twitckbot.extensions.channel.ChannelExtension
import fr.o80.twitckbot.extensions.commands.RuntimeCommandExtension
import fr.o80.twitckbot.extensions.help.HelpExtension
import fr.o80.twitckbot.extensions.market.MarketExtension
import fr.o80.twitckbot.extensions.overlay.OverlayExtension
import fr.o80.twitckbot.extensions.points.PointsExtension
import fr.o80.twitckbot.extensions.repeat.RepeatExtension
import fr.o80.twitckbot.extensions.rewards.RewardsExtension
import fr.o80.twitckbot.extensions.shoutout.ShoutOutExtension
import fr.o80.twitckbot.extensions.sound.SoundExtension
import fr.o80.twitckbot.extensions.welcome.WelcomeExtension
import fr.o80.twitckbot.service.help.Help
import fr.o80.twitckbot.service.overlay.Overlay
import fr.o80.twitckbot.service.points.Points
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.system.Extension

@Module
interface ExtensionBindsModule {
    @Binds
    fun bindHelp(impl: HelpExtension): Help

    @Binds
    fun bindSound(impl: SoundExtension): Sound

    @Binds
    fun bindOverlay(impl: OverlayExtension): Overlay

    @Binds
    fun bindPoints(impl: PointsExtension): Points

    @Binds
    @IntoSet
    fun bindChannelExtension(impl: ChannelExtension): Extension

    @Binds
    @IntoSet
    fun bindHelpExtension(impl: HelpExtension): Extension

    @Binds
    @IntoSet
    fun bindMarketExtension(impl: MarketExtension): Extension

    @Binds
    @IntoSet
    fun bindOverlayExtension(impl: OverlayExtension): Extension

    @Binds
    @IntoSet
    fun bindPointsExtension(impl: PointsExtension): Extension

    @Binds
    @IntoSet
    fun bindRepeatExtension(impl: RepeatExtension): Extension

    @Binds
    @IntoSet
    fun bindShoutOutExtension(impl: ShoutOutExtension): Extension

    @Binds
    @IntoSet
    fun bindRemoteActions(impl: RemoteActionsExtension): Extension

    @Binds
    @IntoSet
    fun bindRuntimeCommand(impl: RuntimeCommandExtension): Extension

    @Binds
    @IntoSet
    fun bindSoundExtension(impl: SoundExtension): Extension

    @Binds
    @IntoSet
    fun bindViewerRewardsExtension(impl: RewardsExtension): Extension

    @Binds
    @IntoSet
    fun bindWelcomeExtension(impl: WelcomeExtension): Extension
}
