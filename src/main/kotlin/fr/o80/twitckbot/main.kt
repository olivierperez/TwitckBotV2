package fr.o80.twitckbot

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import fr.o80.twitckbot.navigation.NavHostComponent
import fr.o80.twitckbot.theme.TwitckBotTheme

fun main() {
    val lifecycle = LifecycleRegistry()
    val root = NavHostComponent(DefaultComponentContext(lifecycle))
    val state = WindowState(
        width = 1024.dp,
        height = 768.dp
    )

    singleWindowApplication(
        state = state,
        title = "TwitckBot v2.0"
    ) {
        TwitckBotTheme {
            root.render()
        }
    }
}
