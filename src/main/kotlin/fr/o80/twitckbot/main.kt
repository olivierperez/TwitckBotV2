package fr.o80.twitckbot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import fr.o80.twitckbot.navigation.DefaultRootComponent
import fr.o80.twitckbot.navigation.RootComponent
import fr.o80.twitckbot.theme.TwitckBotTheme
import fr.o80.twitckbot.utils.runOnUiThread

fun main() {
    val lifecycle = LifecycleRegistry()
    val root = runOnUiThread {
        DefaultRootComponent(DefaultComponentContext(lifecycle))
    }

    singleWindowApplication(
        state = WindowState(width = 1024.dp, height = 768.dp),
        title = "TwitckBot v2.0"
    ) {
        TwitckBotTheme {
            RootContent(
                component = root,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(fade()),
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.Dashboard -> child.component.render()
            is RootComponent.Child.Onboarding -> child.component.render()
        }
    }
}
