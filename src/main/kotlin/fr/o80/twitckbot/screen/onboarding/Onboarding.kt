package fr.o80.twitckbot.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.o80.twitckbot.data.model.FullAuth
import fr.o80.twitckbot.screen.molecule.PasswordTextField
import fr.o80.twitckbot.screen.molecule.PortTextField


@Composable
fun Onboarding(
    broadcasterName: String?,
    fullAuth: FullAuth?,
    onAuthorizationClicked: (streamerName: String, port: Int, clientId: String, clientSecret: String) -> Unit,
    onCopyAuthorizationUrlClicked: (streamerName: String, port: Int, clientId: String, clientSecret: String) -> Unit,
    onGenerateClientCredentialsClicked: () -> Unit
) {
    var streamerName by remember { mutableStateOf(broadcasterName ?: "") }
    var clientId by remember { mutableStateOf("") }
    var clientSecret by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("9015") }
    val scroll = rememberScrollState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().verticalScroll(scroll)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            TextField(
                value = streamerName,
                maxLines = 1,
                label = { Text(text = "Your channel name") },
                onValueChange = { streamerName = it }
            )

            if (fullAuth == null) {
                Spacer(modifier = Modifier.height(15.dp))
                Divider()
                Spacer(modifier = Modifier.height(15.dp))

                OutlinedButton(
                    onClick = { onGenerateClientCredentialsClicked() }
                ) {
                    Text("Generate Client ID and secret")
                }

                Spacer(modifier = Modifier.height(15.dp))
                PasswordTextField(
                    value = clientId,
                    label = { Text(text = "Client ID") },
                    onValueChange = { clientId = it }
                )

                Spacer(modifier = Modifier.height(30.dp))
                PasswordTextField(
                    value = clientSecret,
                    label = { Text(text = "Client secret") },
                    onValueChange = { clientSecret = it }
                )

                Spacer(modifier = Modifier.height(15.dp))
                Divider()
                Spacer(modifier = Modifier.height(15.dp))

                PortTextField(
                    value = port,
                    label = { Text(text = "Authentication port") },
                    onValueChange = { port = it }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "OAuth Redirect URL: http://localhost:$port/oauth",
                    style = MaterialTheme.typography.caption
                )

                Spacer(modifier = Modifier.height(15.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onAuthorizationClicked(
                                streamerName,
                                port.toInt(),
                                clientId,
                                clientSecret
                            )
                        }
                    ) {
                        Text("Authorize access to Twitch")
                    }
                    OutlinedButton(
                        onClick = {
                            onCopyAuthorizationUrlClicked(
                                streamerName,
                                port.toInt(),
                                clientId,
                                clientSecret
                            )
                        }
                    ) {
                        Text("Copy URL")
                    }
                }
            }
        }
    }
}