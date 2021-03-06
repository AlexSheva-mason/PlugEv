package com.shevaalex.android.plugev.presentation.mapscreen.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.shevaalex.android.plugev.domain.openchargemap.API_RESULT_LIMIT
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.openchargemap.usecase.GetChargeStationListUseCase
import com.shevaalex.android.plugev.domain.postcode.model.PostCode
import com.shevaalex.android.plugev.domain.postcode.usecase.GetLocationForPostcodeUseCase
import com.shevaalex.android.plugev.presentation.common.ui.BaseViewModel
import com.shevaalex.android.plugev.presentation.common.ui.UiState
import com.shevaalex.android.plugev.presentation.common.ui.uiErrorRetrofitException
import com.shevaalex.android.plugev.presentation.common.ui.uiInfoResultsLimited
import com.shevaalex.android.plugev.presentation.mapscreen.*
import com.shevaalex.android.plugev.presentation.mapscreen.viewstate.MapScreenViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel
@Inject constructor(
    private val getChargeStationListUseCase: GetChargeStationListUseCase,
    private val getLocationForPostcodeUseCase: GetLocationForPostcodeUseCase
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
                        onFilterRowStateChange(
                            option = intent.option,
                            isEnabledState = intent.isEnabledState
                        )
                    }
                    is MapScreenIntent.SetLocationFromPostcode -> {
                        setLocationFromPostcode(intent.postcode.trim())
                    }
                    is MapScreenIntent.SearchBarStateChange -> setSearchbarState(intent.textFieldValue)
                    is MapScreenIntent.PostcodeLocationHandled -> setLocationHandled()
                    is MapScreenIntent.SearchBarClearState -> resetSearchBarState()
                    is MapScreenIntent.ConsumeUiInfoSnack -> resetUiInfo()
                    is MapScreenIntent.ConsumeUiErrorSnack -> resetUiError()
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
                        )
                    )
                }
                is DataResult.Error -> setErrorState(result)
            }
        }
    }

    private fun onShowBottomSheet(id: String) {
        val infoObject = state.value.chargingStations.find { it.id == id }
        infoObject?.let {
            setState(
                state.value.copy(
                    isLoading = false,
                    bottomSheetViewState = BottomSheetViewState(chargingStation = it)
                )
            )
        }
    }

    private fun onHideBottomSheet() {
        setState(
            state.value.copy(
                bottomSheetViewState = null
            )
        )
    }

    private suspend fun onFilterRowStateChange(option: FilterOption, isEnabledState: Boolean) {
        val newOption = getNewFilterOptionWithState(option, isEnabledState)

        val filterOption = state.value.filteringRowState.optionsList.find {
            it.filterType == newOption.filterType &&
                    it.optionIds == newOption.optionIds &&
                    it.text == newOption.text
        }

        filterOption?.let {
            if (!isEnabledState) {

                if (shouldDisableOption(filterOption)) {
                    replaceOptionInFilterStateSet(newOption)
                } else {
                    enableAllFilterOptionsOfType(it.filterType)
                }

            } else replaceOptionInFilterStateSet(newOption)
        }

        onShowChargersForMapPosition(
            zoom = state.value.cameraZoom,
            longitude = state.value.cameraPosition.longitude,
            latitude = state.value.cameraPosition.latitude,
            distance = state.value.fetchRadiusMiles ?: 2f,
        )
    }

    private fun getNewFilterOptionWithState(option: FilterOption, newState: Boolean): FilterOption {
        return when (option) {
            is FilterOption.Level1 -> FilterOption.Level1(isEnabled = newState)
            is FilterOption.Level2 -> FilterOption.Level2(isEnabled = newState)
            is FilterOption.Level3 -> FilterOption.Level3(isEnabled = newState)
            is FilterOption.Public -> FilterOption.Public(isEnabled = newState)
            is FilterOption.Private -> FilterOption.Private(isEnabled = newState)
        }
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

    private fun replaceOptionInFilterStateSet(option: FilterOption) {
        val newSet = state.value.filteringRowState.optionsList.map {
            if (it.filterType == option.filterType &&
                it.text == option.text &&
                it.optionIds == option.optionIds
            ) {
                option
            } else it
        }.toSet()

        setNewFilterRowState(
            newSet = newSet,
            resetBottomSheetState = option.chipState == ChipState.Disabled
        )
    }

    private fun enableAllFilterOptionsOfType(filterType: FilterType) {
        val newSet = state.value.filteringRowState.optionsList.map { filterOption ->
            when (filterType) {
                FilterType.PowerLevel -> {
                    when (filterOption) {
                        is FilterOption.Level1 -> FilterOption.Level1(isEnabled = true)
                        is FilterOption.Level2 -> FilterOption.Level2(isEnabled = true)
                        is FilterOption.Level3 -> FilterOption.Level3(isEnabled = true)
                        is FilterOption.Private -> filterOption
                        is FilterOption.Public -> filterOption
                    }
                }
                FilterType.Accessibility -> {
                    when (filterOption) {
                        is FilterOption.Level1 -> filterOption
                        is FilterOption.Level2 -> filterOption
                        is FilterOption.Level3 -> filterOption
                        is FilterOption.Private -> FilterOption.Private(isEnabled = true)
                        is FilterOption.Public -> FilterOption.Public(isEnabled = true)
                    }
                }
            }
        }.toSet()

        setNewFilterRowState(newSet)
    }

    private fun setNewFilterRowState(
        newSet: Set<FilterOption>,
        resetBottomSheetState: Boolean = false
    ) {
        setState(
            state.value.copy(
                bottomSheetViewState = if (resetBottomSheetState) {
                    null
                } else state.value.bottomSheetViewState,
                filteringRowState = FilterRowState(optionsList = newSet)
            )
        )
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

    private suspend fun setLocationFromPostcode(postCodeString: String) {
        if (!validatePostCodeForRequest(postCode = postCodeString)) {
            setState(
                state.value.copy(
                    fetchError = UiState.UiError("Invalid postcode request")
                )
            )
            return
        }
        setState(
            state.value.copy(
                isLoading = true
            )
        )
        getLocationForPostcodeUseCase(postCodeString).also { result ->
            when (result) {
                is DataResult.Success -> {
                    when (result.data) {
                        is PostCode.PostCodeSuccess -> {
                            setState(
                                state.value.copy(
                                    cameraZoom = 15f,
                                    cameraPosition = result.data.position,
                                    isLoading = false,
                                    shouldHandlePostcodeLocation = true,
                                )
                            )
                        }
                        is PostCode.PostCodeError -> {
                            setState(
                                state.value.copy(
                                    isLoading = false,
                                    fetchError = UiState.UiError(result.data.error)
                                )
                            )
                        }
                    }
                }
                is DataResult.Error -> setErrorState(errorResult = result)
            }
        }
    }

    private fun setErrorState(errorResult: DataResult.Error) {
        setState(
            state.value.copy(
                isLoading = false,
                fetchError = uiErrorRetrofitException(errorResult.e)
            )
        )
    }

    private fun setSearchbarState(newTextFieldValue: TextFieldValue) {
        val newText = returnValidatedTextForInput(newTextFieldValue.text)
        setState(
            state.value.copy(
                searchBarState = newTextFieldValue.copy(text = newText)
            )
        )
    }

    private fun resetSearchBarState() {
        setState(
            state.value.copy(
                searchBarState = TextFieldValue()
            )
        )
    }

    private fun setLocationHandled() {
        setState(
            state.value.copy(
                shouldHandlePostcodeLocation = false
            )
        )
    }

    private fun resetUiInfo() {
        setState(
            state.value.copy(
                uiMessage = null
            )
        )
    }

    private fun resetUiError() {
        setState(
            state.value.copy(
                fetchError = null,
            )
        )
    }

}
