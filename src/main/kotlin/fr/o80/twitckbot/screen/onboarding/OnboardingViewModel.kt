package fr.o80.twitckbot.screen.onboarding

import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.service.config.LoadConfig
import fr.o80.twitckbot.service.oauth.AuthenticateOnTwitch
import fr.o80.twitckbot.service.system.BrowseUrl
import fr.o80.twitckbot.service.system.CopyToClipboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val authenticateOnTwitch: AuthenticateOnTwitch,
    private val loadConfig: LoadConfig,
    private val browseUrl: BrowseUrl,
    private val copyToClipboard: CopyToClipboard
) {

    val state: StateFlow<UiState> get() = _state
    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    fun init() {
        val config = loadConfig()
        if (config.isComplete()) {
            _state.value = UiState.Loaded(config)
        } else {
            _state.value = UiState.AuthenticationForm(config.broadcasterName, config.auth)
        }
    }

    fun authenticate(
        port: Int,
        clientId: String,
        clientSecret: String,
        onAuthCompleted: (FullAuth) -> Unit,
        onAuthFailed: (Exception) -> Unit
    ) {
        val url = authenticateOnTwitch(port, clientId, clientSecret, onAuthCompleted, onAuthFailed)
        browseUrl(url)
    }

    fun copyAuthorizationUrl(
        port: Int,
        clientId: String,
        clientSecret: String,
        onAuthCompleted: (FullAuth) -> Unit,
        onAuthFailed: (Exception) -> Unit
    ) {
        val url = authenticateOnTwitch(port, clientId, clientSecret, onAuthCompleted, onAuthFailed)
        copyToClipboard(url)
    }

    fun generateClientCredentials() {
        browseUrl("https://dev.twitch.tv/console/apps/create")
    }

    sealed interface UiState {
        object Loading : UiState

        class Loaded(
            val config: Config
        ) : UiState

        class AuthenticationForm(
            val broadcasterName: String?,
            val fullAuth: FullAuth?
        ) : UiState
    }
}
