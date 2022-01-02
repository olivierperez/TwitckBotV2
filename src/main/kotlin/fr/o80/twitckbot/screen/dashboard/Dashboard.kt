package fr.o80.twitckbot.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.o80.twitckbot.screen.dashboard.widget.ConnectableStatusImage

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
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Extensions chargées :")
            state.extensions.forEach { extension ->
                Text(extension::class.java.simpleName)
            }
        }
    }
}
