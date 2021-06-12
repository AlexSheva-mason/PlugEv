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
import com.shevaalex.android.plugev.domain.usecase.GetFilteredChargingStationsUseCase
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

    @MockK
    private lateinit var getFilteredChargingStations: GetFilteredChargingStationsUseCase

    private lateinit var cut: MapScreenViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        cut = MapScreenViewModel(
            getChargeStationListUseCase = getChargeStationListUseCase,
            getFilteredChargingStations = getFilteredChargingStations,
            requestValidator = MapScreenRequestValidator()
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
            getChargeStationListUseCase.invoke(any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()

        cut.submitIntent(intent)

        coVerify { getChargeStationListUseCase(any(), any(), intent.distance) }
    }

    @Test
    fun `should set ViewState's cameraPosition from ShowChargingStationsForCurrentMapPosition intent`() {
        //GIVEN
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

        //WHEN
        val intent = MapScreenIntent.ShowChargingStationsForCurrentMapPosition(
            zoom = 0f,
            latitude = 1.234,
            longitude = 3.456,
            distance = 0f
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
            getChargeStationListUseCase.invoke(any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

        //WHEN
        val intent = MapScreenIntent.ShowChargingStationsForCurrentMapPosition(
            zoom = 1234567f,
            latitude = 0.0,
            longitude = 0.0,
            distance = 0f
        )
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
            getChargeStationListUseCase.invoke(any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

        //WHEN
        val intent = MapScreenIntent.ShowChargingStationsForCurrentMapPosition(
            zoom = 0f,
            latitude = 0.0,
            longitude = 0.0,
            distance = 123456f
        )
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
            getChargeStationListUseCase.invoke(any(), any(), any())
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
    fun `submitting ShowFilteredChargingStationsForLocation intent calls GetFilteredChargingStationsUseCase`() {
        //GIVEN
        coEvery {
            getFilteredChargingStations.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )
        val intent = MapScreenIntent
            .ShowFilteredChargingStationsForLocation(listOf("1"), listOf("2"))

        //WHEN
        cut.submitIntent(intent)

        //THEN
        coVerify {
            getFilteredChargingStations.invoke(any(), any(), any(), listOf("1"), listOf("2"))
        }
    }

    @Test
    fun `should set viewState isLoading false after calling getFilteredChargingStations`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getFilteredChargingStations.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intent = MapScreenIntent
            .ShowFilteredChargingStationsForLocation(null, null)

        //WHEN
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                isLoading = false
            )
        )
    }

    @Test
    fun `viewState should contain list after getFilteredChargingStations returns DataResultSuccess with a list`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getFilteredChargingStations.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intent = MapScreenIntent
            .ShowFilteredChargingStationsForLocation(null, null)

        //WHEN
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                chargingStations = list
            )
        )
    }

    @Test
    fun `viewState should contain UiInfo when getFilteredChargingStations DataResultSuccess has limited list`() {
        //GIVEN
        val list = List(API_RESULT_LIMIT) { DataFactory.getChargingStationDomainModel() }
        coEvery {
            getFilteredChargingStations.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intent = MapScreenIntent
            .ShowFilteredChargingStationsForLocation(null, null)

        //WHEN
        cut.submitIntent(intent)

        //THEN
        val expectedInfoMessage = uiInfoResultsLimited(true, API_RESULT_LIMIT)
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                uiMessage = expectedInfoMessage
            )
        )
    }

    @Test
    fun `viewState should contain empty list when getFilteredChargingStations returns DataResultSuccess empty list`() {
        //GIVEN
        val list = listOf<ChargingStation>()
        coEvery {
            getFilteredChargingStations.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Success(
            data = list
        )
        val intent = MapScreenIntent
            .ShowFilteredChargingStationsForLocation(null, null)

        //WHEN
        cut.submitIntent(intent)

        //THEN
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                chargingStations = listOf()
            )
        )
    }

    @Test
    fun `viewState should contain UiError when getFilteredChargingStations returns error`() {
        //GIVEN
        val exception = Exception("Test exception")
        coEvery {
            getFilteredChargingStations.invoke(any(), any(), any(), any(), any())
        } returns DataResult.Error(
            e = exception
        )
        val intent = MapScreenIntent
            .ShowFilteredChargingStationsForLocation(null, null)

        //WHEN
        cut.submitIntent(intent)

        //THEN
        val expectedError = uiErrorRetrofitException(exception)
        assertThat(cut.state.value).isEqualTo(
            cut.state.value.copy(
                fetchError = expectedError
            )
        )
    }

}
