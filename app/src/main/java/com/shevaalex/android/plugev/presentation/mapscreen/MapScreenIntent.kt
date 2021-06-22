package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.ui.text.input.TextFieldValue

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

    data class SearchBarStateChange(
        val textFieldValue: TextFieldValue
    ) : MapScreenIntent()

    object PostcodeLocationHandled : MapScreenIntent()

    object SearchBarClearState : MapScreenIntent()

}
