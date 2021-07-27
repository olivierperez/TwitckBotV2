package fr.o80.twitckbot.di

import dagger.Module
import dagger.Provides
import fr.o80.twitckbot.data.model.Auth
import fr.o80.twitckbot.data.model.FullAuth

@Module
class AuthenticatedModule {
    @Provides
    @BotAuth
    fun provideBotAuth(fullAuth: FullAuth): Auth = fullAuth.botAuth

    @Provides
    @BroadcasterAuth
    fun provideBroadcasterAuth(fullAuth: FullAuth): Auth = fullAuth.broadcasterAuth
}
