package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.CoroutinesTestRule
import com.shevaalex.android.plugev.data.DataFactory
import com.shevaalex.android.plugev.data.DataFactory.getMapScreenIntentShowChargingStationsForCurrentMapPosition
import com.shevaalex.android.plugev.data.openchargemap.network.model.toDomainModel
import com.shevaalex.android.plugev.data.postcodesio.network.model.toDomainModel
import com.shevaalex.android.plugev.domain.openchargemap.API_RESULT_LIMIT
import com.shevaalex.android.plugev.domain.openchargemap.model.ChargingStation
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.openchargemap.usecase.GetChargeStationListUseCase
import com.shevaalex.android.plugev.domain.postcode.usecase.GetLocationForPostcodeUseCase
import com.shevaalex.android.plugev.presentation.common.ui.UiState
import com.shevaalex.android.plugev.presentation.common.ui.uiErrorRetrofitException
import com.shevaalex.android.plugev.presentation.common.ui.uiInfoResultsLimited
import com.shevaalex.android.plugev.presentation.mapscreen.viewmodel.MapScreenViewModel
import com.shevaalex.android.plugev.presentation.mapscreen.viewstate.MapScreenViewState
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class MapScreenViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @MockK
    private lateinit var getChargeStationListUseCase: GetChargeStationListUseCase

    @MockK
    private lateinit var getLocationForPostCodeUseCase: GetLocationForPostcodeUseCase

    private lateinit var cut: MapScreenViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        cut = MapScreenViewModel(
            getChargeStationListUseCase = getChargeStationListUseCase,
            getLocationForPostcodeUseCase = getLocationForPostCodeUseCase
        )
    }

    @Test
    fun `viewModel has initially a default viewState`() {
        val expectedViewState = MapScreenViewState(
            cameraPosition = LatLng(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE),
            cameraZoom = MAP_DEFAULT_ZOOM,
            fetchRadiusMiles = null,
            chargingStations = listOf(),
            isLoading = true,
            uiMessage = null,
            fetchError = null,
            bottomSheetViewState = null,
            filteringRowState = FilterRowState(),
            searchBarState = TextFieldValue(),
            searchBarInteractionSource = cut.state.value.searchBarInteractionSource,
            shouldHandlePostcodeLocation = false,
        )
        assertThat(cut.state.value).isEqualTo(expectedViewState)
    }

    @Test
    fun `submitting ShowChargingStationsForCurrentMapPosition intent calls getChargeStationListUseCase`() {
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()

        cut.submitIntent(intent)

        coVerify { getChargeStationListUseCase(any(), any(), intent.distance, any(), any()) }
    }

    @Test
    fun `should call getChargeStationListUseCase with filtering options null by default`() {
        //GIVEN @Before

        //WHEN
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase(
                any(), any(), any(), null, null
            )
        }
    }

    @Test
    fun `should set ViewState's cameraPosition from ShowChargingStationsForCurrentMapPosition intent`() {
        //GIVEN @Before

        //WHEN
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition(
            latitude = 1.234,
            longitude = 3.456
        )
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                cameraPosition = LatLng(intent.latitude, intent.longitude)
            )
        )
    }

    @Test
    fun `should set ViewState's cameraZoom from ShowChargingStationsForCurrentMapPosition intent`() {
        //GIVEN @Before

        //WHEN
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition(zoom = 1234567f)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                cameraZoom = 1234567f
            )
        )
    }

    @Test
    fun `should set ViewState's fetchRadiusMiles from ShowChargingStationsForCurrentMapPosition intent`() {
        //GIVEN @Before

        //WHEN
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition(distance = 123456f)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                fetchRadiusMiles = 123456f
            )
        )
    }

    @Test
    fun `should set ViewState's isLoading false after calling getChargeStationListUseCase`() {
        //GIVEN @Before

        //WHEN
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                isLoading = false
            )
        )
    }

    @Test
    fun `viewState should contain appropriate list from getChargeStationListUseCase DataResultSuccess`() {
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )

        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                chargingStations = list
            )
        )
    }

    @Test
    fun `viewState should contain UiInfo when getChargeStationListUseCase DataResultSuccess has limited list`() {
        val list = List(API_RESULT_LIMIT) { DataFactory.getChargingStationDomainModel() }
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )

        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        val expectedInfoMessage = uiInfoResultsLimited(true, API_RESULT_LIMIT)
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                uiMessage = expectedInfoMessage
            )
        )
    }

    @Test
    fun `viewState should contain empty list when getChargeStationListUseCase DataResultSuccess has empty list`() {
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                chargingStations = listOf()
            )
        )
    }

    @Test
    fun `viewState should contain UiError when getChargeStationListUseCase returns error`() {
        val exception = Exception("Test exception")
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Error(exception)

        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        val expectedErrorMessage = uiErrorRetrofitException(exception)
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                fetchError = expectedErrorMessage
            )
        )
    }

    @Test
    fun `viewState should have isLoading false after submitting ShowBottomSheetWithInfo intent`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intentShowChargingPoints = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intentShowChargingPoints)
        val intentShowBottomSheet = MapScreenIntent.ShowBottomSheetWithInfo("id51.72215137119824")

        //WHEN
        cut.submitIntent(intentShowBottomSheet)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                isLoading = false
            )
        )
    }

    @Test
    fun `should set bottomSheetInfoObject in viewState when submitting ShowBottomSheetWithInfo intent`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intentShowChargingPoints = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intentShowChargingPoints)
        val intentShowBottomSheet = MapScreenIntent.ShowBottomSheetWithInfo("id51.72215137119824")

        //WHEN
        cut.submitIntent(intentShowBottomSheet)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                bottomSheetViewState = BottomSheetViewState(
                    chargingStation = ChargingStation(
                        id = "id51.72215137119824",
                        usageCost = "usageCost",
                        addressTitle = "addressInfoTitle",
                        addressLine1 = "addressLine1",
                        addressLine2 = "addressLine2",
                        town = "town",
                        province = "province",
                        postCode = "postCode",
                        distanceMiles = "1.23",
                        latitude = 51.72215137119824,
                        longitude = 0.047445889881063685,
                        usageTypeTitle = "usageTypeTitle",
                        isPayAtLocation = true,
                        isMembershipRequired = true,
                        statusTypeTitle = "statusTypeTitle",
                        isOperationalStatus = true,
                        connections = listOf(
                            DataFactory.getConnectionNetworkDto("1", 3.0, 1),
                            DataFactory.getConnectionNetworkDto("2", 7.0, 1)
                        ).map { it.toDomainModel() },
                        totalNumberOfPoints = 2
                    )
                )
            )
        )
    }

    @Test
    fun `should set bottomSheetInfoObject to null when receiving HideBottomSheet intent`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intentShowChargingPoints = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intentShowChargingPoints)
        val intentShowBottomSheet = MapScreenIntent.ShowBottomSheetWithInfo("id51.72215137119824")
        cut.submitIntent(intentShowBottomSheet)

        //WHEN
        val intentHide = MapScreenIntent.HideBottomSheet
        cut.submitIntent(intentHide)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                bottomSheetViewState = null
            )
        )
    }

    @Test
    fun `should set view state's Level1 filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Level1()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList)
            .contains(FilterOption.Level1(isEnabled = false))
    }

    @Test
    fun `should set view state's Level2 filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Level2()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList)
            .contains(FilterOption.Level2(false))
    }

    @Test
    fun `should set view state's Level3 filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Level3()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList)
            .contains(FilterOption.Level3(false))
    }

    @Test
    fun `should set view state's Public filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Public()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList)
            .contains(FilterOption.Public(false))
    }

    @Test
    fun `should set view state's Private filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Private()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList)
            .contains(FilterOption.Private(false))
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Level1`() {
        //GIVEN
        val filterOption = FilterOption.Level1()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), listOf("2", "3"), any())
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Level2`() {
        //GIVEN
        val filterOption = FilterOption.Level2()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), listOf("1", "3"), any())
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Level3`() {
        //GIVEN
        val filterOption = FilterOption.Level3()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), listOf("1", "2"), any())
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Public`() {
        //GIVEN
        val filterOption = FilterOption.Public()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), listOf("2", "3", "6"))
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Private`() {
        //GIVEN
        val filterOption = FilterOption.Private()

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption, false)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(
                any(),
                any(),
                any(),
                any(),
                listOf("1", "4", "5", "7")
            )
        }
    }

    @Test
    fun `should enable all filtering options of FilterType PowerLevel if all disabled`() {
        //GIVEN
        val filterLevel1 = FilterOption.Level1()
        val filterLevel2 = FilterOption.Level2()
        val filterLevel3 = FilterOption.Level3()

        //WHEN
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel1, false))
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel2, false))
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel3, false))

        //THEN
        val disabledOption = cut.state.value.filteringRowState.optionsList.find { filterOption ->
            filterOption.filterType == filterLevel1.filterType &&
                    filterOption.chipState == ChipState.Disabled
        }
        assertThat(disabledOption).isNull()
    }

    @Test
    fun `should enable all filtering options of FilterType Accessibility if all disabled`() {
        //GIVEN
        val filterPublic = FilterOption.Public()
        val filterPrivate = FilterOption.Private()

        //WHEN
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterPublic, false))
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterPrivate, false))

        //THEN
        val disabledOption = cut.state.value.filteringRowState.optionsList.find { filterOption ->
            filterOption.filterType == filterPublic.filterType &&
                    filterOption.chipState == ChipState.Disabled
        }
        assertThat(disabledOption).isNull()
    }

    @Test
    fun `should not nullify bottom sheet state when new filtering state enabled`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intentShowChargingPoints = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intentShowChargingPoints)
        val intentShowBottomSheet = MapScreenIntent.ShowBottomSheetWithInfo("id51.72215137119824")
        cut.submitIntent(intentShowBottomSheet)

        //WHEN
        val filterLevel1 = FilterOption.Level1()
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel1, true))

        //THEN
        assertThat(cut.state.value.bottomSheetViewState).isNotNull()
    }

    @Test
    fun `should nullify bottom sheet state when new filtering state disabled`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intentShowChargingPoints = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intentShowChargingPoints)
        val intentShowBottomSheet = MapScreenIntent.ShowBottomSheetWithInfo("id51.72215137119824")
        cut.submitIntent(intentShowBottomSheet)

        //WHEN
        val filterLevel1 = FilterOption.Level1()
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel1, false))

        //THEN
        assertThat(cut.state.value.bottomSheetViewState).isNull()
    }

    @Test
    fun `should call location for post code use case`() {
        //GIVEN
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        coVerify {
            getLocationForPostCodeUseCase.invoke(any())
        }
    }

    @Test
    fun `should set isLoading true when calling post code use case`() {
        //GIVEN
        val intentChargeStations = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intentChargeStations)

        //WHEN
        val intentPostCode = MapScreenIntent.SetLocationFromPostcode("123456")
        cut.submitIntent(intent = intentPostCode)

        //THEN
        assertThat(cut.state.value.isLoading).isTrue()
    }

    @Test
    fun `should set camera position after fetching post code location`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto(postCodeName = "CB6 3NW", latitude = 2.0, longitude = 2.0)
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.cameraPosition).isEqualTo(LatLng(2.0, 2.0))
    }

    @Test
    fun `should set isLoading false along with camera position`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto(postCodeName = "CB6 3NW", latitude = 2.0, longitude = 2.0)
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.isLoading).isFalse()
    }

    @Test
    fun `should set zoom after fetching post code location`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto(postCodeName = "CB6 3NW", latitude = 2.0, longitude = 2.0)
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.cameraZoom).isEqualTo(15f)
    }

    @Test
    fun `should set shouldHandlePostcodeLocation true after fetching post code location`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto(postCodeName = "CB6 3NW", latitude = 2.0, longitude = 2.0)
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.shouldHandlePostcodeLocation).isTrue()
    }

    @Test
    fun `should set geolocation error message after fetching post code location`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto(postCodeName = "CB6 3NW")
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.fetchError)
            .isEqualTo(UiState.UiError("Geolocation for this postcode is not available"))
    }

    @Test
    fun `should set invalid postcode error message after fetching post code location`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto()
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.fetchError)
            .isEqualTo(UiState.UiError("Invalid postcode"))
    }

    @Test
    fun `should set isLoading false along with error message`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto(postCodeName = "CB6 3NW")
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.isLoading).isFalse()
    }

    @Test
    fun `should set error message if retrofit call failed`() {
        //GIVEN
        val exception = Exception("Test exception")
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Error(exception)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent)

        //THEN
        val expectedErrorMessage = uiErrorRetrofitException(exception)
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                fetchError = expectedErrorMessage
            )
        )
    }

    @Test
    fun `should set isLoading false if retrofit call failed`() {
        //GIVEN
        val exception = Exception("Test exception")
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Error(exception)
        val intent = MapScreenIntent.SetLocationFromPostcode("123456")

        //WHEN
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.isLoading).isFalse()
    }

    @Test
    fun `should set search bar view state`() {
        //GIVEN
        val intent =
            MapScreenIntent.SearchBarStateChange(textFieldValue = TextFieldValue(text = "TEST"))

        //WHEN
        cut.submitIntent(intent = intent)

        //THEN
        assertThat(cut.state.value.searchBarState).isEqualTo(TextFieldValue(text = "TEST"))
    }

    @Test
    fun `should handle postcode location consuming intent`() {
        //GIVEN
        val postCodeInfo = DataFactory
            .getPostCodeDto(postCodeName = "CB6 3NW", latitude = 2.0, longitude = 2.0)
            .toDomainModel()
        coEvery {
            getLocationForPostCodeUseCase.invoke(any())
        } returns DataResult.Success(postCodeInfo)
        val intentFetchLocation = MapScreenIntent.SetLocationFromPostcode("")
        cut.submitIntent(intent = intentFetchLocation)

        //WHEN
        val intentHandleLocation = MapScreenIntent.PostcodeLocationHandled
        cut.submitIntent(intent = intentHandleLocation)

        //THEN
        assertThat(cut.state.value.shouldHandlePostcodeLocation).isFalse()
    }

    @Test
    fun `should reset search bar state when calling intent that clears search bar state`() {
        //GIVEN
        val setSearchBarState =
            MapScreenIntent.SearchBarStateChange(textFieldValue = TextFieldValue(text = "TEST"))
        cut.submitIntent(intent = setSearchBarState)

        //WHEN
        val clearSearchBarState = MapScreenIntent.SearchBarClearState
        cut.submitIntent(intent = clearSearchBarState)

        //THEN
        assertThat(cut.state.value.searchBarState).isEqualTo(TextFieldValue())
    }

    @Test
    fun `should reset snack bar ui info message to null`() {
        //GIVEN
        val list = List(API_RESULT_LIMIT) { DataFactory.getChargingStationDomainModel() }
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        //WHEN
        val clearInfoSnackIntent = MapScreenIntent.ConsumeUiInfoSnack
        cut.submitIntent(clearInfoSnackIntent)

        //THEN
        assertThat(cut.state.value.uiMessage).isNull()
    }

    @Test
    fun `should reset snack bar ui error message to null`() {
        //GIVEN
        val exception = Exception("Test exception")
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Error(exception)
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        //WHEN
        val clearErrorSnackIntent = MapScreenIntent.ConsumeUiErrorSnack
        cut.submitIntent(clearErrorSnackIntent)

        //THEN
        assertThat(cut.state.value.fetchError).isNull()
    }

}
