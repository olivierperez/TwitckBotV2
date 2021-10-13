package fr.o80.twitckbot.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun Onboarding(
    onAuthorizationClicked: (port: Int, clientId: String, clientSecret: String) -> Unit,
    onGenerateClientCredentialsClicked: () -> Unit
) {
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
                value = clientId,
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation('*'),
                label = { Text(text = "Client ID") },
                onValueChange = { clientId = it }
            )

            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                value = clientSecret,
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation('*'),
                label = { Text(text = "Client secret") },
                onValueChange = { clientSecret = it }
            )

            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                value = port,
                maxLines = 1,
                label = { Text(text = "Authentication port") },
                onValueChange = {
                    if (it.matches("^\\d*$".toRegex()) && it.toInt() in 1..65535) {
                        port = it
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text="OAuth Redirect URL: http://localhost:$port/oauth",
                style = MaterialTheme.typography.caption
            )

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { onAuthorizationClicked(port.toInt(), clientId, clientSecret) }
            ) {
                Text("Authorize access to Twitch")
            }

            OutlinedButton(
                onClick = { onGenerateClientCredentialsClicked() }
            ) {
                Text("Generate Client ID and Client secret")
            }
        }
    }
}