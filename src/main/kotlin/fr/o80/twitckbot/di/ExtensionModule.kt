package fr.o80.twitckbot.di

import dagger.Module
import dagger.Provides
import fr.o80.twitckbot.Extensions
import fr.o80.twitckbot.extensions.repeat.RepeatExtension
import fr.o80.twitckbot.extensions.sound.SoundExtension
import fr.o80.twitckbot.extensions.welcome.WelcomeExtension

@Module
class ExtensionModule {
    @Provides
    fun provideExtensions(
        sound: SoundExtension,
        welcome: WelcomeExtension,
        repeat: RepeatExtension
    ): Extensions = Extensions(listOf(repeat, sound, welcome))
}
