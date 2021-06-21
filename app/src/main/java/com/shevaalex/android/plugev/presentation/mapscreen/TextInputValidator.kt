package com.shevaalex.android.plugev.presentation.mapscreen

import java.util.*

fun returnValidatedTextForInput(input: String): String {
    if (input.isBlank()) return input
    return input
        .filter {
            it.isLetterOrDigit() || it.isWhitespace()
        }
        .toUpperCase(Locale.getDefault())
        .take(8)
}
