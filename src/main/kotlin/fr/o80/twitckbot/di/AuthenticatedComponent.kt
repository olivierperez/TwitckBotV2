package fr.o80.twitckbot.di

import dagger.BindsInstance
import dagger.Subcomponent
import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.screen.dashboard.DashboardComponent

@SessionScope
@Subcomponent(modules = [
    AuthenticatedModule::class,
    AuthenticatedBindsModule::class,
    ExtensionBindsModule::class
])
interface AuthenticatedComponent {

    fun inject(dashboardComponent: DashboardComponent)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun withConfig(config: Config): Builder

        fun build(): AuthenticatedComponent
    }
}
