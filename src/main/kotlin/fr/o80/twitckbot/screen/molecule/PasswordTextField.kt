package fr.o80.twitckbot.screen.molecule

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit
) {
    TextField(
        value = value,
        maxLines = 1,
        visualTransformation = PasswordVisualTransformation('*'),
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = onValueChange
    )
}
