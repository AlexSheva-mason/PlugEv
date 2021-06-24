package com.shevaalex.android.plugev.presentation.mapscreen.viewmodel

import java.util.*

fun returnValidatedTextForInput(input: String): String {
    if (input.isBlank()) return input.take(8)
    return input
        .filter {
            it.isLetterOrDigit() || it.isWhitespace()
        }
        .toUpperCase(Locale.getDefault())
        .take(8)
}
