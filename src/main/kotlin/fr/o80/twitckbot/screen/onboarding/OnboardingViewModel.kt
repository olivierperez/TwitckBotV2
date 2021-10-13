package fr.o80.twitckbot.screen.onboarding

import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.service.oauth.AuthenticateOnTwitch
import fr.o80.twitckbot.service.oauth.LoadAuthentication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.awt.Desktop
import java.net.URI
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val authenticateOnTwitch: AuthenticateOnTwitch,
    private val loadAuthentication: LoadAuthentication
) {

    val state: StateFlow<UiState> get() = _state
    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    val fullAuth: StateFlow<FullAuth?> get() = _fullAuth
    private val _fullAuth: MutableStateFlow<FullAuth?> = MutableStateFlow(null)

    fun init() {
        val auth = loadAuthentication.invoke()
        if (auth != null) {
            _fullAuth.value = auth
        } else {
            _state.value = UiState.AuthenticationForm
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

        if (Desktop.isDesktopSupported() &&
            Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
        ) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            System.err.println("System cannot open URL: $url")
        }
    }

    fun generateClientCredentials() {
        val url = "https://dev.twitch.tv/console/apps/create"
        if (Desktop.isDesktopSupported() &&
            Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
        ) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            System.err.println("System cannot open URL: $url")
        }
    }

    sealed interface UiState {
        object Loading : UiState
        object AuthenticationForm : UiState
    }
}
