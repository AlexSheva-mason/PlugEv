package com.shevaalex.android.plugev.presentation.mapscreen.viewmodel


fun validatePostCodeForRequest(postCode: String): Boolean {
    return postCode.length > 5
}
