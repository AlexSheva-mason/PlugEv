package com.shevaalex.android.plugev.presentation.mapscreen

sealed class MapScreenIntent {

    data class ShowChargingStationsForCurrentMapPosition(
        val zoom: Float,
        val latitude: Double,
        val longitude: Double,
        val distance: Float,
        val levelIds: List<String>?,
        val usageTypeIds: List<String>?
    ) : MapScreenIntent()

    data class ShowBottomSheetWithInfo(
        val id: String
    ) : MapScreenIntent()

    object HideBottomSheet : MapScreenIntent()

}
