package com.shevaalex.android.plugev.presentation.mapscreen

fun returnValidatedTextForInput(input: String): String {
    if (input.isBlank()) return input
    return input.filter {
        it.isLetterOrDigit() || it.isWhitespace()
    }
}
