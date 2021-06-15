package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.service.googlemap.PlugEvClusterManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * iterates list and checks if item is already in ClusterManager Algorithm's Collection
 * adds an item if it's not present in the Collection
 */
suspend fun addItemsToCollection(
    chargingStationList: List<ChargingStation>,
    latLngBounds: LatLngBounds,
    evClusterManager: PlugEvClusterManager
) = withContext(Dispatchers.Default) {
    for (chargingStation in chargingStationList) {
        ensureActive()
        val position = LatLng(chargingStation.latitude, chargingStation.longitude)
        if (latLngBounds.contains(position)) {
            val existentItem = evClusterManager.algorithm.items.find { clusterItem ->
                (clusterItem as ChargingStation).id == chargingStation.id
            }
            //if item is not in a list of cluster items -> add
            if (existentItem == null) {
                evClusterManager.addItem(chargingStation)
            }
        }
    }
}

suspend fun removeItemsNotPresentInViewState(
    chargingStationList: List<ChargingStation>,
    evClusterManager: PlugEvClusterManager
) = withContext(Dispatchers.Default) {
    evClusterManager.algorithm.items.forEach { algrorithmItem ->
        ensureActive()
        val existentItem = chargingStationList.find { viewStateItem ->
            viewStateItem.id == algrorithmItem.id
        }
        if (existentItem == null) evClusterManager.removeItem(algrorithmItem)
    }
}
