package fr.o80.twitckbot.screen.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.di.RootDiComponent
import fr.o80.twitckbot.navigation.Component
import javax.inject.Inject

class OnboardingComponent(
    rootDiComponent: RootDiComponent,
    private val onAuthentication: (Config) -> Unit
) : Component {

    @Inject
    lateinit var viewModel: OnboardingViewModel

    init {
        rootDiComponent.inject(this)
    }

    @Composable
    override fun render() {
        LaunchedEffect(viewModel) {
            viewModel.init()
            viewModel.state.collect { state ->
                if (state is OnboardingViewModel.UiState.Loaded) {
                    onAuthentication(state.config)
                }
            }
        }

        val state by viewModel.state.collectAsState()
        OnboardingContent(state)
    }

    @Composable
    private fun OnboardingContent(state: OnboardingViewModel.UiState) {
        when (state) {
            is OnboardingViewModel.UiState.AuthenticationForm -> AuthenticationForm(
                state.broadcasterName,
                state.fullAuth
            )
            OnboardingViewModel.UiState.Loading -> Loading()
            is OnboardingViewModel.UiState.Loaded -> AuthenticationForm(
                "TODO redirect",
                null
            )
        }
    }

    @Composable
    private fun AuthenticationForm(
        broadcasterName: String?,
        fullAuth: FullAuth?
    ) {
        Onboarding(
            broadcasterName,
            fullAuth,
            onAuthorizationClicked = { streamerName, port, clientId, clientSecret ->
                viewModel.authenticate(
                    streamerName,
                    port,
                    clientId,
                    clientSecret,
                    onAuthCompleted = { fullAuth ->
                        onAuthentication(Config(streamerName, fullAuth))
                    },
                    onAuthFailed = { it.printStackTrace() }
                )
            },
            onCopyAuthorizationUrlClicked = { streamerName, port, clientId, clientSecret ->
                viewModel.copyAuthorizationUrl(
                    streamerName,
                    port,
                    clientId,
                    clientSecret,
                    onAuthCompleted = { fullAuth ->
                        onAuthentication(Config(streamerName, fullAuth))
                    },
                    onAuthFailed = { it.printStackTrace() }
                )
            },
            onGenerateClientCredentialsClicked = {
                viewModel.generateClientCredentials()
            }
        )
    }
}
