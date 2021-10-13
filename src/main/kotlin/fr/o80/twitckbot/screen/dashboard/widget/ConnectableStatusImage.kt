package fr.o80.twitckbot.screen.dashboard.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.o80.twitckbot.OnClick
import fr.o80.twitckbot.screen.dashboard.DashboardViewModel
import fr.o80.twitckbot.service.connectable.ConnectableStatus

@Composable
fun ConnectableStatusImage(
    connectableState: DashboardViewModel.ConnectableState,
    onRetry: () -> Unit
) {

    when (connectableState.status) {
        ConnectableStatus.CONNECTING -> {
            InnerStatusImage(
                connectableState,
                "${connectableState.name} is connecting",
                Color.Blue
            )
        }
        ConnectableStatus.NOT_CONNECTED -> {
            InnerStatusImage(
                connectableState,
                "${connectableState.name} is disconnected",
                Color.Red,
                onRetry
            )
        }
        ConnectableStatus.CONNECTED -> {
            InnerStatusImage(
                connectableState,
                "${connectableState.name} is connected",
                Color.Green
            )
        }
    }
}

@Composable
private fun InnerStatusImage(
    connectableState: DashboardViewModel.ConnectableState,
    contentDescription: String,
    borderColor: Color,
    onClick: OnClick? = null
) {
    Image(
        painter = painterResource(connectableState.icon),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .applyOnClick(onClick)
            .padding(4.dp)
    )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.applyOnClick(onClick: (() -> Unit)?): Modifier {
    var hover by remember { mutableStateOf(false) }
    val background by animateColorAsState(if (hover) Color(0x33000000) else Color(0x00000000))

    return if (onClick != null) {
        this.pointerMoveFilter(
            onEnter = {
                hover = true
                true
            },
            onExit = {
                hover = false
                true
            }
        )
            .background(background)
            .clickable(onClick = onClick)
    } else {
        this
    }
}
