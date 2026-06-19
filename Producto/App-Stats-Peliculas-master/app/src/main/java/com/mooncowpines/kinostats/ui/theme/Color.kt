package com.mooncowpines.kinostats.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val KinoWhite = Color(0xFFFFFFFF)
val KinoGray = Color(0xFFCCCCCC)
val KinoOffWhite = Color(0xFFD5D5D5)

val KinoTransparentBlack = Color(0x00313131)
val KinoLighterGray = Color(0x41474444)
val KinoDarkGray = Color(0xFF1E1E1E)
val KinoBlack = Color(0xFF121212)

val KinoYellow = Color(0xFFFFC040)
val KinoDarkYellow = Color(0xFFFB9600)

val KinoYellowVerticalGradient = Brush.verticalGradient(
    colors = listOf(
        KinoYellow.copy(alpha = 0.55f),
        KinoBlack,
        KinoBlack
    )
)

val KinoYellowHorizontalGradient = Brush.horizontalGradient(
    colors = listOf(
        KinoYellow.copy(alpha = 0.55f),
        KinoYellow.copy(alpha = 0.55f),
        KinoBlack,
        KinoBlack
    )
)




