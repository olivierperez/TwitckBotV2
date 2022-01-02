package fr.o80.twitckbot.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import fr.o80.twitckbot.extensions.repeat.RepeatExtension
import fr.o80.twitckbot.extensions.sound.SoundExtension
import fr.o80.twitckbot.extensions.welcome.WelcomeExtension
import fr.o80.twitckbot.service.sound.Sound
import fr.o80.twitckbot.system.Extension

@Module
interface ExtensionBindsModule {
    @Binds
    fun bindSound(impl: SoundExtension): Sound

    @Binds
    @IntoSet
    fun bindRepeatExtension(impl: RepeatExtension): Extension

    @Binds
    @IntoSet
    fun bindSoundExtension(impl: SoundExtension): Extension

    @Binds
    @IntoSet
    fun bindWelcomeExtension(impl: WelcomeExtension): Extension
}
