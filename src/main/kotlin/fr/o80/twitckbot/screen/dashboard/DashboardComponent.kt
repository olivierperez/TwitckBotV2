package fr.o80.twitckbot.screen.dashboard

import androidx.compose.runtime.Composable
import fr.o80.twitckbot.di.AuthenticatedComponent
import fr.o80.twitckbot.navigation.Component
import javax.inject.Inject

class DashboardComponent(
    authenticatedComponent: AuthenticatedComponent
) : Component {

    @Inject
    lateinit var viewModel: DashboardViewModel

    init {
        authenticatedComponent.inject(this)
    }

    @Composable
    override fun render() {
        Dashboard(viewModel)
    }

}
