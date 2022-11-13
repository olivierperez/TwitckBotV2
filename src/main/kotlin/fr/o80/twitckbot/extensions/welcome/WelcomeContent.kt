package fr.o80.twitckbot.extensions.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.o80.twitckbot.system.design.ExtensionCard

@Composable
fun WelcomeContent(
    modifier: Modifier,
    lastWelcomed: List<String>
) {
    ExtensionCard(modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Welcome",
                style = MaterialTheme.typography.overline
            )

            if (lastWelcomed.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Personne")
                }
            } else {
                lastWelcomed.forEach { login ->
                    Text(login)
                }
            }
        }
    }
}
