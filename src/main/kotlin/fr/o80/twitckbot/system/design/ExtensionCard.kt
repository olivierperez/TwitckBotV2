package fr.o80.twitckbot.system.design

import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ExtensionCard(
    modifier: Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
        content = content
    )
}