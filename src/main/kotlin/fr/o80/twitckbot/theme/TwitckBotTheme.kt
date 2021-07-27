package fr.o80.twitckbot.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TwitckBotTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}
