package fr.o80.twitckbot.di

import dagger.Binds
import dagger.Module
import fr.o80.twitckbot.internal.step.StepsExecutorImpl
import fr.o80.twitckbot.internal.storage.InFileStorageExtension
import fr.o80.twitckbot.internal.twitch.TwitchApiImpl
import fr.o80.twitckbot.service.connectable.chat.IrcClient
import fr.o80.twitckbot.service.connectable.chat.IrcMessenger
import fr.o80.twitckbot.service.twitch.TwitchApi
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.event.EventBusImpl
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.log.Slf4jLoggerFactory
import fr.o80.twitckbot.service.storage.Storage
import fr.o80.twitckbot.system.step.StepsExecutor

@Module
interface AuthenticatedBindsModule {
    @Binds
    fun bindPipeline(impl: EventBusImpl): EventBus

    @Binds
    fun bindTwitchApi(impl: TwitchApiImpl): TwitchApi

    @Binds
    fun bindLoggerFactory(impl: Slf4jLoggerFactory): LoggerFactory

    @Binds
    fun bindStorage(impl: InFileStorageExtension): Storage

    @Binds
    fun bindIrcMessenger(impl: IrcClient): IrcMessenger

    @Binds
    fun bindStepsExecutor(impl: StepsExecutorImpl): StepsExecutor
}
