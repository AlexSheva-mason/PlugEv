package com.shevaalex.android.plugev.presentation.mapscreen

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

fun getMapIntent(
    latitude: Double,
    longitude: Double,
    packageManager: PackageManager
): Intent? {
    val gmmIntentUri =
        Uri.parse("google.navigation:q=$latitude,$longitude")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    return mapIntent.resolveActivity(packageManager)?.let {
        mapIntent
    }
}
