package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.addMarker
import com.shevaalex.android.plugev.R
import kotlinx.coroutines.launch

@Composable
fun MapScreen(modifier: Modifier) {
    val testLatitude = 51.5026838
    val testLongitude = -0.1166801
    val mapView = rememberMapViewWithLifecycle()
    Column(modifier = modifier) {
        MapViewContainer(
            map = mapView,
            latitude = testLatitude,
            longitude = testLongitude
        )
    }
}

@Composable
fun MapViewContainer(
    latitude: Double,
    longitude: Double,
    map: MapView
) {
    val insets = LocalWindowInsets.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    AndroidView({ map }) { mapView ->
        coroutineScope.launch {
            val googleMap = mapView.awaitMap()
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style)
            )
            googleMap.setPadding(
                0,
                insets.statusBars.top,
                0,
                insets.navigationBars.bottom
            )
            val position = LatLng(latitude, longitude)
            googleMap.addMarker {
                position(position)
            }
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    position,
                    17f
                )
            )
        }
    }
}