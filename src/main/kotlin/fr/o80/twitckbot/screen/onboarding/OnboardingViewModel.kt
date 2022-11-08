package fr.o80.twitckbot.screen.onboarding

import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.service.config.LoadConfig
import fr.o80.twitckbot.service.config.SaveConfig
import fr.o80.twitckbot.service.oauth.AuthenticateOnTwitch
import fr.o80.twitckbot.service.system.BrowseUrl
import fr.o80.twitckbot.service.system.CopyToClipboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val authenticateOnTwitch: AuthenticateOnTwitch,
    private val loadConfig: LoadConfig,
    private val saveConfig: SaveConfig,
    private val browseUrl: BrowseUrl,
    private val copyToClipboard: CopyToClipboard
) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val state: StateFlow<UiState> get() = _state
    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    fun init() {
        scope.launch {
            val config = loadConfig()
            if (config.isComplete()) {
                _state.update { UiState.Loaded(config) }
            } else {
                _state.update { UiState.AuthenticationForm(config.broadcasterName, config.auth) }
            }
        }
    }

    fun authenticate(
        broadcasterName: String,
        port: Int,
        clientId: String,
        clientSecret: String,
        onAuthCompleted: (FullAuth) -> Unit,
        onAuthFailed: (Exception) -> Unit
    ) {
        val url = authenticateOnTwitch(
            port,
            clientId,
            clientSecret,
            onAuthCompleted = { fullAuth ->
                scope.launch {
                    saveConfig(broadcasterName, fullAuth)
                    onAuthCompleted(fullAuth)
                }
            },
            onAuthFailed
        )
        browseUrl(url)
    }

    fun copyAuthorizationUrl(
        broadcasterName: String,
        port: Int,
        clientId: String,
        clientSecret: String,
        onAuthCompleted: (FullAuth) -> Unit,
        onAuthFailed: (Exception) -> Unit
    ) {
        val url = authenticateOnTwitch(
            port,
            clientId,
            clientSecret,
            onAuthCompleted = { fullAuth ->
                scope.launch {
                    saveConfig(broadcasterName, fullAuth)
                    onAuthCompleted(fullAuth)
                }
            },
            onAuthFailed
        )
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
