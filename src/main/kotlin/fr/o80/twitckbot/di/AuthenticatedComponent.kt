package fr.o80.twitckbot.di

import dagger.BindsInstance
import dagger.Subcomponent
import fr.o80.twitckbot.data.model.FullAuth
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
        fun withAuth(fullAuth: FullAuth): Builder

        fun build(): AuthenticatedComponent
    }
}
