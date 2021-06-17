package com.shevaalex.android.plugev.presentation.mapscreen.viewstate

import com.google.android.gms.maps.model.LatLng
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.presentation.common.ui.UiState
import com.shevaalex.android.plugev.presentation.mapscreen.*

data class MapScreenViewState(
    val cameraPosition: LatLng = LatLng(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE),
    val cameraZoom: Float = MAP_DEFAULT_ZOOM,
    val fetchRadiusMiles: Float? = null,
    val chargingStations: List<ChargingStation> = listOf(),
    val isLoading: Boolean = true,
    val uiMessage: UiState.UiInfo? = null,
    val fetchError: UiState.UiError? = null,
    val bottomSheetViewState: BottomSheetViewState? = null,
    val filteringRowState: FilterRowState = FilterRowState()
) {

    override fun toString(): String {
        return "MapScreenViewState(\n cameraPosition=$cameraPosition," +
                " \n cameraZoom=$cameraZoom," +
                " \n fetchRadiusMiles=$fetchRadiusMiles," +
                " \n chargingStations=${chargingStations.size}," +
                " \n isLoading=$isLoading," +
                " \n uiMessage=$uiMessage," +
                " \n fetchError=$fetchError," +
                " \n bottomSheetInfoObject=$bottomSheetViewState," +
                " \n filteringRowState=$filteringRowState)"
    }

}
