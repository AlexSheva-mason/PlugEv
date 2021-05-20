package com.shevaalex.android.plugev.service.googlemap

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager

class PlugEvClusterManager(
    context: Context,
    googleMap: GoogleMap,
    private val onCameraIdle: () -> Unit
) : ClusterManager<ClusterItem>(context, googleMap) {

    override fun onCameraIdle() {
        super.onCameraIdle()
        onCameraIdle.invoke()
    }

}
