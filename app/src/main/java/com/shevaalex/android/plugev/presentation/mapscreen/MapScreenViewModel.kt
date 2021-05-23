package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.shevaalex.android.plugev.domain.API_RESULT_LIMIT
import com.shevaalex.android.plugev.domain.model.DataResult
import com.shevaalex.android.plugev.domain.usecase.GetChargeStationListUseCase
import com.shevaalex.android.plugev.presentation.common.ui.BaseViewModel
import com.shevaalex.android.plugev.presentation.common.ui.uiErrorRetrofitException
import com.shevaalex.android.plugev.presentation.common.ui.uiInfoResultsLimited
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel
@Inject constructor(
    private val getChargeStationListUseCase: GetChargeStationListUseCase
) : BaseViewModel<MapScreenViewState>(
    initialState = MapScreenViewState()
) {

    private val pendingIntent = MutableSharedFlow<MapScreenIntent>()

    init {
        viewModelScope.launch {
            pendingIntent.collect { intent ->
                when (intent) {
                    is MapScreenIntent.ShowChargingStationsForCurrentMapPosition -> onShowChargersForMapPosition(
                        zoom = intent.zoom,
                        latitude = intent.latitude,
                        longitude = intent.longitude,
                        distance = intent.distance
                    )
                }
            }
        }
    }

    private suspend fun onShowChargersForMapPosition(
        zoom: Float,
        longitude: Double,
        distance: Float,
        latitude: Double
    ) {
        if (validateNewPositionForRequest(latitude, longitude, zoom)) {
            viewModelScope.launch {
                setState(
                    state.value.copy(
                        isLoading = true
                    )
                )
                getChargeStationListUseCase(
                    latitude = latitude,
                    longitude = longitude,
                    distance = distance
                ).also { result ->
                    when (result) {
                        is DataResult.Success -> {
                            setState(
                                state.value.copy(
                                    cameraZoom = zoom,
                                    cameraPosition = LatLng(latitude, longitude),
                                    chargingStations = result.data,
                                    isLoading = false,
                                    uiMessage = uiInfoResultsLimited(
                                        isResultLimitReached = result.data.size == API_RESULT_LIMIT,
                                        limit = API_RESULT_LIMIT
                                    )
                                )
                            )
                        }
                        is DataResult.Error -> {
                            setState(
                                state.value.copy(
                                    cameraZoom = zoom,
                                    cameraPosition = LatLng(latitude, longitude),
                                    isLoading = false,
                                    fetchError = uiErrorRetrofitException(result.e)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun submitIntent(intent: MapScreenIntent) {
        viewModelScope.launch { pendingIntent.emit(intent) }
    }

    /**
     * To prevent a new request being made when clicking on the same marker or a marker nearby:
     * @returns true if a new request should be made, based on:
     * MapScreenViewState.cameraPosition distance to a new camera position -> should be more than 25m OR
     * MapScreenViewState.chargingStations -> list is empty OR
     * MapScreenViewState.cameraZoom -> has been zoomed out
     */
    private fun validateNewPositionForRequest(
        latitude: Double,
        longitude: Double,
        zoom: Float
    ): Boolean {
        val distanceM = SphericalUtil
            .computeDistanceBetween(
                LatLng(state.value.cameraPosition.latitude, state.value.cameraPosition.longitude),
                LatLng(latitude, longitude)
            )
            .toInt()
        val zoomedOut = state.value.cameraZoom > zoom
        return state.value.chargingStations.isEmpty() || distanceM > 25 || zoomedOut
    }

}
