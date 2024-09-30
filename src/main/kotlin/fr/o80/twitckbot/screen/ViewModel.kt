package fr.o80.twitckbot.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class ViewModel {

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    abstract fun init()

    fun stop() {
        scope.cancel()
    }
}

@Composable
fun <T : ViewModel> T.remembered(): T {
    DisposableEffect(this) {
        init()
        onDispose { stop() }
    }

    return remember { this }
}
