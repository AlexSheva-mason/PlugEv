package com.shevaalex.android.plugev.service.googlemap

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import com.shevaalex.android.plugev.domain.model.ChargingStation

class PlugEvClusterManager(
    context: Context,
    googleMap: GoogleMap,
    private val onMarkerClick: (id: String) -> Unit,
    private val onCameraIdle: () -> Unit
) : ClusterManager<ChargingStation>(context, googleMap) {

    init {
        renderer = PlugEvClusterRenderer(
            context = context,
            googleMap = googleMap,
            clusterManager = this
        )
    }

    override fun onCameraIdle() {
        super.onCameraIdle()
        onCameraIdle.invoke()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag
        if(tag is String) {
            onMarkerClick.invoke(tag)
        }
        return super.onMarkerClick(marker)
    }

}
