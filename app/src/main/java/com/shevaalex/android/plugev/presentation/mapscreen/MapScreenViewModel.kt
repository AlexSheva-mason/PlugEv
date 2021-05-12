package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.domain.model.DataResult
import com.shevaalex.android.plugev.domain.usecase.GetChargeStationListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel
@Inject constructor(
    private val getChargeStationListUseCase: GetChargeStationListUseCase
) : ViewModel() {

    var chargingStations: List<ChargingStation> by mutableStateOf(listOf())
        private set

    fun onMapStateChange(latitude: Double, longitude: Double, distance: Float) {
        viewModelScope.launch {
            getChargeStationListUseCase(
                latitude = latitude,
                longitude = longitude,
                distance = distance
            ).also { result ->
                when (result) {
                    is DataResult.Success -> {
                        chargingStations = result.data
                    }
                    is DataResult.Error -> {

                    }
                }
            }
        }
    }

}
