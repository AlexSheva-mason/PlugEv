package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.shevaalex.android.plugev.domain.API_RESULT_LIMIT
import com.shevaalex.android.plugev.domain.model.DataResult
import com.shevaalex.android.plugev.domain.usecase.GetChargeStationListUseCase
import com.shevaalex.android.plugev.presentation.common.ui.BaseViewModel
import com.shevaalex.android.plugev.presentation.common.ui.uiErrorRetrofitException
import com.shevaalex.android.plugev.presentation.common.ui.uiInfoResultsLimited
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel
@Inject constructor(
    private val getChargeStationListUseCase: GetChargeStationListUseCase,
    private val requestValidator: MapScreenRequestValidator
) : BaseViewModel<MapScreenViewState>(
    initialState = MapScreenViewState()
) {
    private val pendingIntent = MutableSharedFlow<MapScreenIntent>()

    init {
        viewModelScope.launch {
            pendingIntent.collect { intent ->
                when (intent) {
                    is MapScreenIntent.ShowChargingStationsForCurrentMapPosition -> {
                        onShowChargersForMapPosition(
                            zoom = intent.zoom,
                            latitude = intent.latitude,
                            longitude = intent.longitude,
                            distance = intent.distance
                        )
                    }
                    is MapScreenIntent.ShowBottomSheetWithInfo -> onShowBottomSheet(id = intent.id)
                    is MapScreenIntent.HideBottomSheet -> onHideBottomSheet()
                    is MapScreenIntent.FilterOptionStateChange -> {
                        onFilterRowStateChange(option = intent.filterOption)
                    }
                }
            }
        }
    }

    fun submitIntent(intent: MapScreenIntent) {
        viewModelScope.launch { pendingIntent.emit(intent) }
    }

    private suspend fun onShowChargersForMapPosition(
        zoom: Float,
        longitude: Double,
        distance: Float,
        latitude: Double
    ) {
        if (requestValidator.validateNewPositionForRequest(
                state.value,
                latitude,
                longitude,
                zoom
            )
        ) {
            viewModelScope.launch {
                setState(
                    state.value.copy(
                        cameraZoom = zoom,
                        cameraPosition = LatLng(latitude, longitude),
                        fetchRadiusMiles = distance,
                        isLoading = true
                    )
                )
                getChargeStationListUseCase(
                    latitude = latitude,
                    longitude = longitude,
                    distance = distance,
                    levelIds = getFilteringLevelIds(),
                    usageTypeIds = getFilteringUsageTypeIds()
                ).also { result ->
                    when (result) {
                        is DataResult.Success -> {
                            setState(
                                state.value.copy(
                                    chargingStations = result.data,
                                    isLoading = false,
                                    uiMessage = uiInfoResultsLimited(
                                        isResultLimitReached = result.data.size == API_RESULT_LIMIT,
                                        limit = API_RESULT_LIMIT
                                    ),
                                    fetchError = null
                                )
                            )
                        }
                        is DataResult.Error -> {
                            setState(
                                state.value.copy(
                                    isLoading = false,
                                    uiMessage = null,
                                    fetchError = uiErrorRetrofitException(result.e)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onShowBottomSheet(id: String) {
        setState(
            state.value.copy(
                isLoading = false,
                bottomSheetInfoObject = state.value.chargingStations.find { it.id == id }
            )
        )
    }

    private fun onHideBottomSheet() {
        setState(
            state.value.copy(
                bottomSheetInfoObject = null
            )
        )
    }

    private suspend fun onFilterRowStateChange(option: FilterOption) {
        val filterOption = state.value.filteringRowState.optionsList.find {
            when (option) {
                is FilterOption.Level1 -> it is FilterOption.Level1
                is FilterOption.Level2 -> it is FilterOption.Level2
                is FilterOption.Level3 -> it is FilterOption.Level3
                is FilterOption.Public -> it is FilterOption.Public
                is FilterOption.Private -> it is FilterOption.Private
            }
        }

        filterOption?.let {
            if (option.chipState == ChipState.Disabled) {

                if (shouldDisableOption(filterOption)) {
                    filterOption.chipState = option.chipState
                } else {
                    enableAllFilterOptionOfType(it.filterType)
                }

            } else filterOption.chipState = option.chipState
        }

        onShowChargersForMapPosition(
            zoom = state.value.cameraZoom,
            longitude = state.value.cameraPosition.longitude,
            latitude = state.value.cameraPosition.latitude,
            distance = state.value.fetchRadiusMiles ?: 2f,
        )
    }

    private fun shouldDisableOption(filterOption: FilterOption): Boolean {
        val totalOptionTypeCount = state.value.filteringRowState.optionsList.count { filter ->
            filter.filterType == filterOption.filterType
        }
        val disabledCount = state.value.filteringRowState.optionsList.count { filter ->
            filter.filterType == filterOption.filterType &&
                    filter.chipState == ChipState.Disabled
        }
        return totalOptionTypeCount - disabledCount != 1
    }

    private fun enableAllFilterOptionOfType(filterType: FilterType) {
        state.value.filteringRowState.optionsList.forEach {
            if (it.filterType == filterType) it.chipState = ChipState.Enabled
        }
    }

    private fun getFilteringLevelIds(): List<String>? {
        val enabledPowerLevelOptions =
            state.value.filteringRowState.optionsList.filter { filterOption ->
                filterOption.filterType == FilterType.PowerLevel
                        && filterOption.chipState == ChipState.Enabled
            }

        val powerLevelOptions = state.value.filteringRowState.optionsList.filter { filterOption ->
            filterOption.filterType == FilterType.PowerLevel
        }
        val disabledOption = powerLevelOptions.find { powerOption ->
            powerOption.chipState == ChipState.Disabled
        }

        return disabledOption?.let {
            val list = enabledPowerLevelOptions.flatMap { powerOption ->
                powerOption.optionIds
            }
            if (list.isNotEmpty()) list
            else null
        }
    }

    private fun getFilteringUsageTypeIds(): List<String>? {
        val enabledUsageOptions = state.value.filteringRowState.optionsList.filter { filterOption ->
            filterOption.filterType == FilterType.Accessibility
                    && filterOption.chipState == ChipState.Enabled
        }

        val accessibilityOptions =
            state.value.filteringRowState.optionsList.filter { filterOption ->
                filterOption.filterType == FilterType.Accessibility
            }

        val disabledOption = accessibilityOptions.find { accessibilityOption ->
            accessibilityOption.chipState == ChipState.Disabled
        }

        return disabledOption?.let {
            val list = enabledUsageOptions.flatMap { accessibilityOption ->
                accessibilityOption.optionIds
            }
            if (list.isNotEmpty()) list
            else null
        }
    }

}
