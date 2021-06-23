package com.shevaalex.android.plugev.presentation.mapscreen


fun validatePostCodeForRequest(postCode: String): Boolean {
    return postCode.length > 5
}
