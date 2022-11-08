package fr.o80.twitckbot.di

import dagger.Binds
import dagger.Module
import fr.o80.twitckbot.data.SettingsStorage
import fr.o80.twitckbot.internal.storage.InFileStorageExtension
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.log.Slf4jLoggerFactory
import fr.o80.twitckbot.service.oauth.AuthStorage
import fr.o80.twitckbot.service.storage.Storage

@Module(subcomponents = [AuthenticatedComponent::class])
interface RootModule {

    @Binds
    fun bindLoggerFactory(impl: Slf4jLoggerFactory): LoggerFactory

    @Binds
    fun bindAuthStorage(settingsStorage: SettingsStorage): AuthStorage

    @Binds
    fun bindStorage(impl: InFileStorageExtension): Storage
}
