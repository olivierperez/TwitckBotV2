package fr.o80.twitckbot.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ExtensionBloc {
    @Composable
    fun render(modifier: Modifier)
}
