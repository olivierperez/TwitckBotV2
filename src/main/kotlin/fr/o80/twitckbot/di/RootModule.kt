package fr.o80.twitckbot.di

import dagger.Binds
import dagger.Module
import fr.o80.twitckbot.data.SettingsStorage
import fr.o80.twitckbot.service.oauth.AuthStorage

@Module(subcomponents = [AuthenticatedComponent::class])
interface RootModule {
    @Binds
    fun bindAuthStorage(settingsStorage: SettingsStorage): AuthStorage
}
