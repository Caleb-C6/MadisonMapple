package com.cs407.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cs407.myapplication.R

// Brand font — Varela Round (Playful, friendly)
val VarelaRound = FontFamily(
    Font(R.font.varela_round_regular, weight = FontWeight.Normal)
)

// Body font — Nunito
val Nunito = FontFamily(
    Font(R.font.nunito_regular, weight = FontWeight.Normal),
    Font(R.font.nunito_semibold, weight = FontWeight.SemiBold)
)

// GLOBAL TYPOGRAPHY
val AppTypography = Typography(

    // Big app titles
    headlineLarge = TextStyle(
        fontFamily = VarelaRound,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = VarelaRound,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),

    // Section titles (e.g., Login, Sign Up, labels)
    titleMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),

    // Body text
    bodyMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    bodySmall = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)
