package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.service.googlemap.PlugEvClusterManager


/**
 * iterates list and checks if item is already in ClusterManager Algorithm's Collection
 * adds an item if it's not present in the Collection
 */
fun addItemsToCollection(
    chargingStationList: List<ChargingStation>,
    latLngBounds: LatLngBounds,
    evClusterManager: PlugEvClusterManager
) {
    for (chargingStation in chargingStationList) {
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

/**
 * iterates through items in ClusterManager Algorithm and purges ones that are not within visibleRegion
 */
fun removeItemFromCollection(
    latLngBounds: LatLngBounds,
    evClusterManager: PlugEvClusterManager
) {
    evClusterManager.algorithm.items.forEach { clusterItem ->
        if (!latLngBounds.contains(clusterItem.position)) {
            evClusterManager.removeItem(clusterItem)
        }
    }
}
