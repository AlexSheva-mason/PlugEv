package com.shevaalex.android.plugev.presentation.mapscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.insets.LocalWindowInsets
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import com.shevaalex.android.plugev.R
import kotlinx.coroutines.launch

//Default Lat / Lng for location (London)
private const val DEFAULT_LATITUDE = 51.5026838
private const val DEFAULT_LONGITUDE = -0.1166801

@Composable
fun MapScreen(modifier: Modifier, locationProviderClient: FusedLocationProviderClient) {
    val mapView = rememberMapViewWithLifecycle()
    Column(modifier = modifier) {
        MapViewContainer(
            map = mapView,
            locationProviderClient = locationProviderClient
        )
    }
}

@Composable
fun MapViewContainer(
    map: MapView,
    locationProviderClient: FusedLocationProviderClient
) {
    val context = LocalContext.current
    var lastKnownPosition by rememberSaveable {
        mutableStateOf(LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE))
    }
    val coroutineScope = rememberCoroutineScope()
    val requestLauncher = rememberLauncher(
        locationProviderClient = locationProviderClient
    ) {
        lastKnownPosition = it
    }
    val insets = LocalWindowInsets.current
    AndroidView({ map }) { mapView ->
        //read value here to trigger recomposition when permission status changes
        val position = lastKnownPosition
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
            checkLocationPermission(
                context = context,
                requestLauncher = requestLauncher,
            ) {
                setLastKnownPosition(locationProviderClient) { lastKnownPosition = it }
                googleMap.isMyLocationEnabled = true
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

private fun checkLocationPermission(
    context: Context,
    requestLauncher: ActivityResultLauncher<String>,
    onHasPermission: () -> Unit
) {
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> {
            onHasPermission()
        }
        else -> {
            requestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Composable
private fun rememberLauncher(
    locationProviderClient: FusedLocationProviderClient,
    onLastLocationChange: (LatLng) -> Unit
): ActivityResultLauncher<String> {
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setLastKnownPosition(locationProviderClient, onLastLocationChange)
        }
    }
}


private fun setLastKnownPosition(
    locationProviderClient: FusedLocationProviderClient,
    onLastLocationChange: (LatLng) -> Unit
) {
    try {
        locationProviderClient.lastLocation.addOnSuccessListener { lastLocation ->
            lastLocation?.let {
                val lastLatLng = LatLng(it.latitude, it.longitude)
                onLastLocationChange(lastLatLng)
            }
        }
    } catch (e: SecurityException) {
        println(e.message)
    }
}
