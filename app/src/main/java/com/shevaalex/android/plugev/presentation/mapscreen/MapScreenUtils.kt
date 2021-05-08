package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * returns distance between LatLng in miles, rounded to 2 decimals
 */
fun computeDistanceMiles(from: LatLng, to: LatLng): Float {
    val distanceMeters = SphericalUtil.computeDistanceBetween(from, to)
    val distanceMiles = distanceMeters.div(1609.344)
    return BigDecimal(distanceMiles).setScale(2, RoundingMode.HALF_EVEN).toFloat()
}