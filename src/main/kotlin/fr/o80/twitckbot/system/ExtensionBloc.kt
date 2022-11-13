package fr.o80.twitckbot.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ExtensionBloc {
    val id: String
    val priority: Int

    @Composable
    fun render(modifier: Modifier)
}
