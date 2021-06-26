package com.shevaalex.android.plugev.presentation.common.ui


sealed class UiState {

    data class UiInfo(
        val message: String
    )

    data class UiError(
        val message: String
    )

}

fun uiInfoResultsLimited(isResultLimitReached: Boolean, limit: Int): UiState.UiInfo? {
    return if (isResultLimitReached) {
        UiState.UiInfo("Results are limited to $limit results, please zoom in to view details")
    } else null
}

fun uiErrorRetrofitException(t: Throwable?): UiState.UiError {
    return UiState.UiError(
        t?.message ?: "Error occurred: $t"
    )
}
