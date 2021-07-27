package fr.o80.twitckbot.di

import dagger.Component
import fr.o80.twitckbot.screen.onboarding.OnboardingComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [RootModule::class])
interface RootComponent {
    fun inject(component: OnboardingComponent)

    val authenticatedComponentBuilder: AuthenticatedComponent.Builder
}
