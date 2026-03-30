package com.example.handify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HandifyColorScheme = lightColorScheme(
    primary = HandifyPrimary,
    background = HandifyBackground,
    surface = HandifySurface,
    onPrimary = HandifyOnPrimary,
    onBackground = HandifyOnBackground,
    onSurface = HandifyOnSurface,
    error = Ember
)

@Composable
fun HandifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HandifyColorScheme,
        typography = HandifyTypography,
        content = content
    )
}
