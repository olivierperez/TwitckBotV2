package fr.o80.twitckbot

import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import fr.o80.twitckbot.navigation.NavHostComponent
import fr.o80.twitckbot.theme.TwitckBotTheme

fun main() {
    val lifecycle = LifecycleRegistry()
    val root = NavHostComponent(DefaultComponentContext(lifecycle))

    singleWindowApplication(title = "TwitckBot v2.0") {
        TwitckBotTheme {
            root.render()
        }
    }
}
