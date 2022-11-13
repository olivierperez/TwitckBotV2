package fr.o80.twitckbot.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.o80.twitckbot.screen.dashboard.widget.ConnectableStatusImage
import fr.o80.twitckbot.system.Extension

@Composable
fun Dashboard(
    viewModel: DashboardViewModel
) {
    val state by viewModel.state.collectAsState(DashboardViewModel.State.EMPTY)

    Scaffold(
        topBar = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.background(Color.Gray).padding(4.dp).fillMaxWidth()
            ) {
                state.connectableStates.forEach {
                    Spacer(Modifier.size(4.dp))
                    ConnectableStatusImage(it, onRetry = { viewModel.retry(it.name) })
                }
            }
        },
        bottomBar = {
            Text("Bottom bar")
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            columns = GridCells.Adaptive(200.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val blocs = state.extensions
                .flatMap(Extension::blocs)
                .sortedWith(ExtensionBlocComparator())
            items(blocs) { extensionBloc ->
                extensionBloc.render(Modifier.size(200.dp))
            }
        }
    }
}
