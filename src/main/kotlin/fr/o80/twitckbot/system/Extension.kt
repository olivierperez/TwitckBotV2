package fr.o80.twitckbot.system

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.o80.twitckbot.system.design.ExtensionCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class Extension {

    protected val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    open val blocs: List<ExtensionBloc> = listOf(EmptyBloc())

    inner class EmptyBloc : ExtensionBloc {
        @Composable
        override fun render(modifier: Modifier) {
            ExtensionCard(
                modifier,
                MaterialTheme.colors.primarySurface.copy(alpha = .5f)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = this@Extension.javaClass.simpleName.removeSuffix("Extension"),
                    style = MaterialTheme.typography.overline,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}
