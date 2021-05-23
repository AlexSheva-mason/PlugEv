package com.shevaalex.android.plugev.presentation.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import com.shevaalex.android.plugev.R
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.service.googlemap.PlugEvClusterManager
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    modifier: Modifier,
    locationProviderClient: FusedLocationProviderClient,
) {
    val viewModel: MapScreenViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()
    val mapView = rememberMapViewWithLifecycle()
    Box {
        MapViewContainer(
            map = mapView,
            locationProviderClient = locationProviderClient,
            viewModel = viewModel,
            chargingStationList = viewState.chargingStations,
            cameraPosition = viewState.cameraPosition,
            cameraZoom = viewState.cameraZoom
        )
        if (viewState.isLoading) {
            LinearProgressIndicator(
                modifier = modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .navigationBarsPadding(bottom = false, left = false, right = true)
            )
        }
    }
}

@SuppressLint("PotentialBehaviorOverride")
@Composable
fun MapViewContainer(
    map: MapView,
    locationProviderClient: FusedLocationProviderClient,
    viewModel: MapScreenViewModel = viewModel(),
    chargingStationList: List<ChargingStation>,
    cameraPosition: LatLng,
    cameraZoom: Float
) {
    val context = LocalContext.current
    var mapInitialized by remember(map) { mutableStateOf(false) }
    val insets = LocalWindowInsets.current
    val progressIndicatorStrokePx =
        with(LocalDensity.current) { ProgressIndicatorDefaults.StrokeWidth.toPx().toInt() }
    val coroutineScope = rememberCoroutineScope()
    var clusterManager: PlugEvClusterManager? by remember { mutableStateOf(null) }
    val requestLauncher = rememberLauncher(
        locationProviderClient = locationProviderClient
    ) {
        coroutineScope.launch {
            val googleMap = map.awaitMap()
            googleMap.isMyLocationEnabled = true
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(it, cameraZoom)
            )
        }
    }
    LaunchedEffect(map, mapInitialized) {
        if (!mapInitialized) {
            val googleMap = map.awaitMap()
            clusterManager = PlugEvClusterManager(context, googleMap) {
                viewModel.submitIntent(
                    MapScreenIntent.ShowChargingStationsForCurrentMapPosition(
                        zoom = googleMap.cameraPosition.zoom,
                        latitude = googleMap.cameraPosition.target.latitude,
                        longitude = googleMap.cameraPosition.target.longitude,
                        distance = computeDistanceMiles(
                            googleMap.cameraPosition.target,
                            googleMap.projection.visibleRegion.farLeft
                        )
                    )
                )
            }
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(cameraPosition, cameraZoom)
            )
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style)
            )
            googleMap.setPadding(
                0,
                (insets.statusBars.top).plus(progressIndicatorStrokePx),
                insets.navigationBars.right,
                insets.navigationBars.bottom
            )
            checkLocationPermission(
                context = context,
                requestLauncher = requestLauncher,
            ) {
                googleMap.isMyLocationEnabled = true
                getLastKnownPosition(locationProviderClient) { lastKnownPosition ->
                    //if viewState has camera position at defaults -> move camera to lastKnownPosition
                    if (cameraPosition == LatLng(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE)) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(lastKnownPosition, cameraZoom)
                        )
                    }
                }
            }
            mapInitialized = true
        }
    }

    AndroidView({ map }) { mapView ->
        coroutineScope.launch {
            val googleMap = mapView.awaitMap()

            clusterManager?.let { evClusterManager ->
                googleMap.setOnCameraIdleListener(evClusterManager)
                googleMap.setOnMarkerClickListener(evClusterManager)
                val latLngBounds = googleMap.projection.visibleRegion.latLngBounds

                addItemsToCollection(
                    chargingStationList = chargingStationList,
                    latLngBounds = latLngBounds,
                    evClusterManager = evClusterManager
                )

                removeItemFromCollection(
                    latLngBounds = latLngBounds,
                    evClusterManager = evClusterManager
                )

                //re-cluster the map
                evClusterManager.cluster()
            }

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
            getLastKnownPosition(locationProviderClient, onLastLocationChange)
        }
    }
}


private fun getLastKnownPosition(
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
