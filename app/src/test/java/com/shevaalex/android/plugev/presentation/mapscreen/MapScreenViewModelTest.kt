package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.CoroutinesTestRule
import com.shevaalex.android.plugev.data.DataFactory
import com.shevaalex.android.plugev.data.DataFactory.getMapScreenIntentShowChargingStationsForCurrentMapPosition
import com.shevaalex.android.plugev.data.network.model.toDomainModel
import com.shevaalex.android.plugev.domain.API_RESULT_LIMIT
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.domain.model.DataResult
import com.shevaalex.android.plugev.domain.usecase.GetChargeStationListUseCase
import com.shevaalex.android.plugev.presentation.common.ui.uiErrorRetrofitException
import com.shevaalex.android.plugev.presentation.common.ui.uiInfoResultsLimited
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

    private lateinit var cut: MapScreenViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        cut = MapScreenViewModel(
            getChargeStationListUseCase = getChargeStationListUseCase
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
            bottomSheetInfoObject = null,
            filteringRowState = FilterRowState()
        )
        assertThat(cut.state.value).isEqualTo(expectedViewState)
    }

    @Test
    fun `submitting ShowChargingStationsForCurrentMapPosition intent calls getChargeStationListUseCase`() {
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()

        cut.submitIntent(intent)

        coVerify { getChargeStationListUseCase(any(), any(), intent.distance, any(), any()) }
    }

    @Test
    fun `should call getChargeStationListUseCase with filtering options null by default`() {
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

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
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

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
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

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
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

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
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

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
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

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
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

        //WHEN
        val intentShowBottomSheet = MapScreenIntent.ShowBottomSheetWithInfo("testID")
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
                bottomSheetInfoObject = ChargingStation(
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
                bottomSheetInfoObject = null
            )
        )
    }

    @Test
    fun `should set view state's Level1 filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Level1(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList).contains(filterOption)
    }

    @Test
    fun `should set view state's Level2 filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Level2(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList).contains(filterOption)
    }

    @Test
    fun `should set view state's Level3 filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Level3(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList).contains(filterOption)
    }

    @Test
    fun `should set view state's Public filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Public(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList).contains(filterOption)
    }

    @Test
    fun `should set view state's Private filtering option when calling FilterOptionStateChange`() {
        //GIVEN
        val filterOption = FilterOption.Private(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value.filteringRowState.optionsList).contains(filterOption)
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Level1`() {
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val filterOption = FilterOption.Level1(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), listOf("2", "3"), any())
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Level2`() {
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val filterOption = FilterOption.Level2(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), listOf("1", "3"), any())
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Level3`() {
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val filterOption = FilterOption.Level3(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), listOf("1", "2"), any())
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Public`() {
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val filterOption = FilterOption.Public(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), listOf("2", "3", "6"))
        }
    }

    @Test
    fun `should call getChargeStationListUseCase with appropriate filtering ids excluding Private`() {
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val filterOption = FilterOption.Private(isEnabled = false)

        //WHEN
        val intent = MapScreenIntent.FilterOptionStateChange(filterOption)
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
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val filterLevel1 = FilterOption.Level1(isEnabled = false)
        val filterLevel2 = FilterOption.Level2(isEnabled = false)
        val filterLevel3 = FilterOption.Level3(isEnabled = false)

        //WHEN
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel1))
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel2))
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterLevel3))

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
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val filterPublic = FilterOption.Public(isEnabled = false)
        val filterPrivate = FilterOption.Private(isEnabled = false)

        //WHEN
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterPublic))
        cut.submitIntent(MapScreenIntent.FilterOptionStateChange(filterPrivate))

        //THEN
        val disabledOption = cut.state.value.filteringRowState.optionsList.find { filterOption ->
            filterOption.filterType == filterPublic.filterType &&
                    filterOption.chipState == ChipState.Disabled
        }
        assertThat(disabledOption).isNull()
    }

}
