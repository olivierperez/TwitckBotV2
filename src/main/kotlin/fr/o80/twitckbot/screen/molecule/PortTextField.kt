package fr.o80.twitckbot.screen.molecule

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType

private val digitRegex = "^\\d*$".toRegex()

@Composable
fun PortTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit
) {
    TextField(
        value = value,
        maxLines = 1,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = {
            if (it.matches(digitRegex) && it.toInt() in 1..65535) {
                onValueChange(it)
            }
        }
    )
}
