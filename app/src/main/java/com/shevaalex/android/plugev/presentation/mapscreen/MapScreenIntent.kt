package com.shevaalex.android.plugev.presentation.mapscreen

sealed class MapScreenIntent {

    data class ShowChargingStationsForCurrentMapPosition(
        val zoom: Float,
        val latitude: Double,
        val longitude: Double,
        val distance: Float
    ) : MapScreenIntent()

    data class ShowBottomSheetWithInfo(
        val id: String
    ) : MapScreenIntent()

    object HideBottomSheet : MapScreenIntent()

    data class FilterOptionStateChange(
        val option: FilterOption,
        val isEnabledState: Boolean
    ) : MapScreenIntent()

    data class SetLocationFromPostcode(
        val postcode: String
    ) : MapScreenIntent()

}
