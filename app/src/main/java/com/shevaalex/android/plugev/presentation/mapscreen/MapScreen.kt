package com.shevaalex.android.plugev.presentation.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import com.shevaalex.android.plugev.R
import com.shevaalex.android.plugev.domain.openchargemap.model.ChargingStation
import com.shevaalex.android.plugev.presentation.mapscreen.viewmodel.MapScreenViewModel
import com.shevaalex.android.plugev.presentation.mapscreen.viewstate.MapScreenViewState
import com.shevaalex.android.plugev.service.googlemap.MapInfoWindowAdapter
import com.shevaalex.android.plugev.service.googlemap.PlugEvClusterManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun MapScreen(
    modifier: Modifier,
    locationProviderClient: FusedLocationProviderClient,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val viewModel: MapScreenViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()

    val mapIntent = remember(viewState.bottomSheetViewState) {
        viewState.bottomSheetViewState?.let {
            getMapIntentForChargingStation(it.chargingStation, context)
        }
    }

    LaunchedEffect(viewState.uiMessage) {
        viewState.uiMessage?.let { uiInfo ->
            handleSnackMessage(
                snackbarHostState = scaffoldState.snackbarHostState,
                message = uiInfo.message,
                duration = SnackbarDuration.Indefinite
            )
            viewModel.submitIntent(MapScreenIntent.ConsumeUiInfoSnack)
        }
    }

    LaunchedEffect(viewState.fetchError) {
        viewState.fetchError?.let { uiError ->
            handleSnackMessage(
                snackbarHostState = scaffoldState.snackbarHostState,
                message = uiError.message,
                duration = SnackbarDuration.Short,
                actionLabel = "error"
            )
            viewModel.submitIntent(MapScreenIntent.ConsumeUiErrorSnack)
        }
    }

    LaunchedEffect(viewState.searchBarInteractionSource) {
        viewState.searchBarInteractionSource.interactions.collectLatest {
            if (it is FocusInteraction.Focus || it is PressInteraction.Press) {
                viewModel.submitIntent(MapScreenIntent.HideBottomSheet)
            }
        }
    }

    BottomSheetScaffold(
        sheetContent = {
            viewState.bottomSheetViewState?.let {
                BottomSheet(
                    bottomSheetViewState = it,
                    modifier = Modifier
                        .navigationBarsPadding(bottom = true, start = true, end = true)
                )
            }
        },
        scaffoldState = scaffoldState,
        snackbarHost = { snackbarHostState ->
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = modifier
                    .widthIn(min = SNACK_WIDTH_MIN.dp, max = SNACK_WIDTH_MAX.dp)
                    .navigationBarsPadding(bottom = true, start = true, end = true)
                    .padding(16.dp),
            ) { data ->
                Snack(
                    message = data.message,
                    isError = data.actionLabel == "error"
                )
            }
        },
        floatingActionButton = if (viewState.bottomSheetViewState != null && mapIntent != null) {
            {
                FloatingActionButton(
                    onClick = { startActivity(context, mapIntent, null) },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fab_navigate),
                        contentDescription = "Action button: navigate to"
                    )
                }
            }
        } else null,
        floatingActionButtonPosition = FabPosition.Center,
        sheetPeekHeight = viewState.bottomSheetViewState?.let {
            BOTTOM_SHEET_PEEK_HEIGHT.dp
        } ?: 0.dp
    ) {
        Box {
            MapViewContainer(
                map = mapView,
                locationProviderClient = locationProviderClient,
                viewState = viewState
            ) { viewModel.submitIntent(it) }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .navigationBarsPadding(bottom = false, start = true, end = true)
            ) {
                if (viewState.isLoading) {
                    LinearProgressIndicator(
                        modifier = modifier
                            .fillMaxWidth()
                    )
                } else Spacer(modifier = Modifier.height(progressBarHeight))
                SearchBar(
                    state = viewState.searchBarState,
                    interactionSource = viewState.searchBarInteractionSource,
                    modifier = modifier
                        .padding(top = SEARCH_BAR_PADDING_TOP.dp),
                    onTextValueChange = {
                        viewModel.submitIntent(MapScreenIntent.SearchBarStateChange(textFieldValue = it))
                    },
                    onSearchRequested = {
                        viewModel.submitIntent(MapScreenIntent.SetLocationFromPostcode(postcode = it))
                    },
                    onClearState = {
                        viewModel.submitIntent(MapScreenIntent.SearchBarClearState)
                    },
                )
                FilteringRow(
                    state = viewState.filteringRowState,
                    modifier = modifier
                        .padding(top = FILTER_ROW_PADDING_VERTICAL.dp)
                ) { filterOption, newState ->
                    viewModel.submitIntent(
                        MapScreenIntent.FilterOptionStateChange(filterOption, newState)
                    )
                }
            }
        }
    }
}

@SuppressLint("PotentialBehaviorOverride", "MissingPermission")
@Composable
fun MapViewContainer(
    map: MapView,
    locationProviderClient: FusedLocationProviderClient,
    viewState: MapScreenViewState,
    submitIntent: (MapScreenIntent) -> Unit
) {
    val context = LocalContext.current
    val insets = LocalWindowInsets.current
    val focusManager = LocalFocusManager.current
    var mapInitialized by remember(map) { mutableStateOf(false) }

    val mapPaddingTopContent = remember {
        getMapPaddingTop()
    }
    val mapPaddingTopContentPx = with(LocalDensity.current) { mapPaddingTopContent.toPx().toInt() }

    val coroutineScope = rememberCoroutineScope()
    var clusterManager: PlugEvClusterManager? by remember(map) { mutableStateOf(null) }
    val requestLauncher = rememberLauncher(
        locationProviderClient = locationProviderClient
    ) {
        coroutineScope.launch {
            val googleMap = map.awaitMap()
            googleMap.isMyLocationEnabled = true
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(it, viewState.cameraZoom)
            )
        }
    }

    LaunchedEffect(viewState.shouldHandlePostcodeLocation) {
        if (viewState.shouldHandlePostcodeLocation) {
            val googleMap = map.awaitMap()
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(viewState.cameraPosition, viewState.cameraZoom)
            ).also {
                submitIntent(MapScreenIntent.PostcodeLocationHandled)
            }
        }
    }

    LaunchedEffect(map, mapInitialized) {
        if (!mapInitialized) {
            val googleMap = map.awaitMap()
            setMyLocationIcon(map, context)
            clusterManager = PlugEvClusterManager(
                context = context,
                googleMap = googleMap,
                onMarkerClick = { id ->
                    imeClearFocus(insets.ime.isVisible, focusManager)
                    submitIntent(MapScreenIntent.ShowBottomSheetWithInfo(id))
                }
            ) {
                submitIntent(
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
            val infoAdapter = MapInfoWindowAdapter(LayoutInflater.from(context))
            clusterManager?.markerCollection?.setInfoWindowAdapter(infoAdapter)
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(viewState.cameraPosition, viewState.cameraZoom)
            )
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style)
            )
            googleMap.setPadding(
                0,
                (insets.statusBars.top).plus(mapPaddingTopContentPx),
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
                    if (viewState.cameraPosition == LatLng(
                            MAP_DEFAULT_LATITUDE,
                            MAP_DEFAULT_LONGITUDE
                        )
                    ) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                lastKnownPosition,
                                viewState.cameraZoom
                            )
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

            googleMap.setOnCameraMoveStartedListener { reason ->
                if (reason == REASON_GESTURE && viewState.bottomSheetViewState != null) {
                    submitIntent(MapScreenIntent.HideBottomSheet)
                }
                imeClearFocus(insets.ime.isVisible, focusManager)
            }

            clusterManager?.let { evClusterManager ->
                googleMap.setOnCameraIdleListener(evClusterManager)
                googleMap.setOnMarkerClickListener(evClusterManager)
                val latLngBounds = googleMap.projection.visibleRegion.latLngBounds

                addItemsToCollection(
                    chargingStationList = viewState.chargingStations,
                    latLngBounds = latLngBounds,
                    evClusterManager = evClusterManager
                )

                removeItemsNotPresentInViewState(
                    chargingStationList = viewState.chargingStations,
                    evClusterManager = evClusterManager
                )

                //re-cluster the map
                evClusterManager.cluster()
            }

        }
    }

    DisposableEffect(map) {
        onDispose {
            clusterManager = null
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


@SuppressLint("MissingPermission")
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

@Composable
fun Snack(message: String, isError: Boolean) {
    Card(
        backgroundColor = MaterialTheme.colors.background,
        elevation = 6.dp
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Spacer(
                modifier = Modifier
                    .weight(0.05f)
                    .fillMaxHeight()
                    .background(
                        if (isError) MaterialTheme.colors.error
                        else MaterialTheme.colors.primary
                    )
            )
            Text(
                text = message,
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .weight(0.95f)
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

private suspend fun handleSnackMessage(
    snackbarHostState: SnackbarHostState,
    message: String,
    duration: SnackbarDuration,
    actionLabel: String? = null
) {
    snackbarHostState.currentSnackbarData?.dismiss()
    snackbarHostState.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = duration
    )
}

private fun getMapIntentForChargingStation(
    chargingStation: ChargingStation,
    context: Context
): Intent? {
    return getMapIntent(
        latitude = chargingStation.latitude,
        longitude = chargingStation.longitude,
        packageManager = context.packageManager
    )
}

private fun getMapPaddingTop(): Dp {
    val searchBarHeight = SEARCH_BAR_PADDING_TOP.dp + searchBarHeight
    val filerRowHeight =
        (FILTER_CHIP_HEIGHT + FILTER_CHIP_PADDING * 2 + FILTER_ROW_PADDING_VERTICAL * 2).dp
    return progressBarHeight + searchBarHeight + filerRowHeight
}

private fun imeClearFocus(
    isVisible: Boolean,
    focusManager: FocusManager
) {
    if (isVisible) {
        focusManager.clearFocus()
    }
}

private fun setMyLocationIcon(
    map: MapView,
    context: Context
) {
    val myLocationIcon: ImageView? = map.findViewWithTag("GoogleMapMyLocationButton")
    myLocationIcon?.setImageDrawable(
        ContextCompat.getDrawable(
            context,
            R.drawable.ic_my_location
        )
    )
    myLocationIcon?.outlineProvider = IconOutlineProvider()
    myLocationIcon?.elevation = 25f
}
