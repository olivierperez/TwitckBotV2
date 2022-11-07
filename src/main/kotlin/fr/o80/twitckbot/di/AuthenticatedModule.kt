package fr.o80.twitckbot.di

import dagger.Module
import dagger.Provides
import fr.o80.twitckbot.data.model.Auth
import fr.o80.twitckbot.data.model.Config

@Module
class AuthenticatedModule {
    @Provides
    @BotAuth
    fun provideBotAuth(config: Config): Auth = config.auth!!.botAuth

    @Provides
    @BroadcasterAuth
    fun provideBroadcasterAuth(config: Config): Auth = config.auth!!.broadcasterAuth

    @Provides
    @BroadcasterName
    fun provideBroadcasterName(config: Config): String = config.broadcasterName!!
}
