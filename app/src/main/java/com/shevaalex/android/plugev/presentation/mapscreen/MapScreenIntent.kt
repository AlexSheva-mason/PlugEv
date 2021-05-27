package com.shevaalex.android.plugev.presentation.mapscreen

sealed class MapScreenIntent {

    data class ShowChargingStationsForCurrentMapPosition(
        val zoom: Float,
        val latitude: Double,
        val longitude: Double,
        val distance: Float
    ) : MapScreenIntent()

}
