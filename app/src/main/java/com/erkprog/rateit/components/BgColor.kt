package com.erkprog.rateit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@Composable
fun BgColor(progress: Float, modifier: Modifier) {
    val hideousColor = Color(0xFFE77A8C)
    val okColor = Color(0xFFF8E1A6)
    val goodColor = Color(0xFF9EE4FF)

    var bgColor by remember { mutableStateOf(okColor) }

    bgColor = if (progress <= 0.5f) {
        lerp(
            start = hideousColor,
            stop = okColor,
            progress * 2f
        )
    } else {
        lerp(
            start = okColor,
            stop = goodColor,
            progress * 2f - 1f
        )
    }
    Box(modifier = modifier.background(color = bgColor))
}
