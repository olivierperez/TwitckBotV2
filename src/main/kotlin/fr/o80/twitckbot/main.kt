package fr.o80.twitckbot

import androidx.compose.desktop.Window
import com.arkivanov.decompose.extensions.compose.jetbrains.rememberRootComponent
import fr.o80.twitckbot.navigation.NavHostComponent
import fr.o80.twitckbot.theme.TwitckBotTheme

fun main() = Window(title = "TwitckBot v2.0") {
    TwitckBotTheme {
        rememberRootComponent(factory = ::NavHostComponent)
            .render()
    }
}
