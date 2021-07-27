package fr.o80.twitckbot.screen.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun Onboarding(
    onAuthorizationClicked: (port: Int, clientId: String, clientSecret: String) -> Unit
) {
    val clientId by remember { mutableStateOf("") }
    val clientSecret by remember { mutableStateOf("") }
    val port by remember { mutableStateOf("9015") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
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
                onValueChange = {}
            )

            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                value = clientSecret,
                maxLines = 1,
                visualTransformation = PasswordVisualTransformation('*'),
                label = { Text(text = "Client secret") },
                onValueChange = {}
            )

            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                value = port,
                maxLines = 1,
                label = { Text(text = "Authentication port") },
                onValueChange = {}
            )

            Spacer(modifier = Modifier.height(30.dp))
            OutlinedButton(
                onClick = { onAuthorizationClicked(port.toInt(), clientId, clientSecret) }
            ) {
                Text("Autoriser l'accès à Twitch")
            }
        }
    }
}