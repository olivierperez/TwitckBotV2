package fr.o80.twitckbot.system

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.o80.twitckbot.system.design.ExtensionCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class Extension {

    protected val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    open val blocs: List<ExtensionBloc> = listOf(EmptyBloc(this::class.java.simpleName))

    inner class EmptyBloc(override val id: String) : ExtensionBloc {

        override val priority: Int = -1

        @Composable
        override fun render(modifier: Modifier) {
            ExtensionCard(
                modifier,
                MaterialTheme.colors.primarySurface.copy(alpha = .5f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = id.removeSuffix("Extension"),
                        style = MaterialTheme.typography.overline,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Default.Check,
                        contentDescription = "Chargé"
                    )
                }
            }
        }
    }

}
