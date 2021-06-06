package com.shevaalex.android.plugev.presentation.common.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.shevaalex.android.plugev.presentation.common.compose.PlugEvTheme
import com.shevaalex.android.plugev.presentation.mapscreen.MapScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.shevaalex.android.plugev.presentation.common.compose.WhiteTrans85
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            ProvideWindowInsets {
                PlugEvTheme {
                    MainScreen(locationProviderClient = fusedLocationProvider)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainScreen(modifier: Modifier = Modifier, locationProviderClient: FusedLocationProviderClient) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    SideEffect {
        systemUiController.setSystemBarsColor(WhiteTrans85, darkIcons = useDarkIcons)
    }
    MapScreen(modifier, locationProviderClient)
}
