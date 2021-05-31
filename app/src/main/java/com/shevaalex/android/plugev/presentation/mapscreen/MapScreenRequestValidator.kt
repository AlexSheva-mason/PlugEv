package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class MapScreenRequestValidator
@Inject
constructor() {

    /**
     * To prevent a new request being made when clicking on the same marker or a marker nearby:
     * @returns true if a new request should be made, based on:
     * MapScreenViewState.cameraPosition distance to a new camera position -> should be more than 25m OR
     * MapScreenViewState.chargingStations -> list is empty OR
     * MapScreenViewState.cameraZoom -> has been zoomed out
     */
    fun validateNewPositionForRequest(
        state: MapScreenViewState,
        latitude: Double,
        longitude: Double,
        zoom: Float
    ): Boolean {
        val distanceM = SphericalUtil
            .computeDistanceBetween(
                LatLng(state.cameraPosition.latitude, state.cameraPosition.longitude),
                LatLng(latitude, longitude)
            )
            .toInt()
        val zoomedOut = state.cameraZoom > zoom
        return state.chargingStations.isEmpty() || distanceM > 25 || zoomedOut
    }

}
