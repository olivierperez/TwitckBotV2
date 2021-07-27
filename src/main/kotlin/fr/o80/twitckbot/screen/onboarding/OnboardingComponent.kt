package fr.o80.twitckbot.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.di.RootComponent
import fr.o80.twitckbot.navigation.Component
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class OnboardingComponent(
    rootComponent: RootComponent,
    private val onAuthentication: (FullAuth) -> Unit
) : Component {

    @Inject
    lateinit var viewModel: OnboardingViewModel

    init {
        rootComponent.inject(this)
    }

    @Composable
    override fun render() {
        LaunchedEffect(viewModel) {
            viewModel.init()
            viewModel.fullAuth.collect { auth -> auth?.let { onAuthentication(auth) } }
        }

        val state by viewModel.state.collectAsState()
        render(state)
    }

    @Composable
    private fun render(state: OnboardingViewModel.UiState) {
        when (state) {
            OnboardingViewModel.UiState.AuthenticationForm -> AuthenticationForm()
            OnboardingViewModel.UiState.Loading -> Loading()
        }
    }

    @Composable
    private fun AuthenticationForm() {
        Onboarding(
            onAuthorizationClicked = { port, clientId, clientSecret ->
                viewModel.authenticate(
                    port,
                    clientId,
                    clientSecret,
                    onAuthCompleted = onAuthentication,
                    onAuthFailed = { it.printStackTrace() }
                )
            }
        )
    }
}
