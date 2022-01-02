package fr.o80.twitckbot.di

import dagger.Binds
import dagger.Module
import fr.o80.twitckbot.extensions.sound.SoundExtension
import fr.o80.twitckbot.service.sound.Sound

@Module
interface ExtensionBindsModule {
    @Binds
    fun bindSound(impl: SoundExtension): Sound
}
