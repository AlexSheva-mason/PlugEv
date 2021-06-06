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
            getChargeStationListUseCase = getChargeStationListUseCase,
            requestValidator = MapScreenRequestValidator()
        )
    }

    @Test
    fun `viewModel has initially a default viewState`() {
        val expectedViewState = MapScreenViewState(
            cameraPosition = LatLng(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE),
            cameraZoom = MAP_DEFAULT_ZOOM,
            chargingStations = listOf(),
            isLoading = true,
            uiMessage = null,
            fetchError = null,
            bottomSheetInfoObject = null
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
    fun `verify state when getChargeStationListUseCase return DataResultSuccess with a list`() {
        val list = listOf(DataFactory.getChargingStationDomainModel())
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any())
        } returns DataResult.Success(
            data = list
        )

        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        assertThat(cut.state.value).isEqualTo(
            MapScreenViewState(
                cameraPosition = LatLng(intent.latitude, intent.longitude),
                cameraZoom = intent.zoom,
                chargingStations = list,
                isLoading = false,
                uiMessage = null,
                fetchError = null,
                bottomSheetInfoObject = null
            )
        )
    }

    @Test
    fun `verify state message when getChargeStationListUseCase return DataResultSuccess with limited list`() {
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
            MapScreenViewState(
                cameraPosition = LatLng(intent.latitude, intent.longitude),
                cameraZoom = intent.zoom,
                chargingStations = list,
                isLoading = false,
                uiMessage = expectedInfoMessage,
                fetchError = null,
                bottomSheetInfoObject = null
            )
        )
    }

    @Test
    fun `verify state when getChargeStationListUseCase return DataResultSuccess empty list`() {
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any())
        } returns DataResult.Success(
            data = listOf()
        )

        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        assertThat(cut.state.value).isEqualTo(
            MapScreenViewState(
                cameraPosition = LatLng(intent.latitude, intent.longitude),
                cameraZoom = intent.zoom,
                chargingStations = listOf(),
                isLoading = false,
                uiMessage = null,
                fetchError = null,
                bottomSheetInfoObject = null
            )
        )
    }

    @Test
    fun `verify state message when getChargeStationListUseCase returns error`() {
        val exception = Exception("Test exception")
        coEvery {
            getChargeStationListUseCase.invoke(any(), any(), any())
        } returns DataResult.Error(exception)

        val intent = getMapScreenIntentShowChargingStationsForCurrentMapPosition()
        cut.submitIntent(intent)

        val expectedErrorMessage = uiErrorRetrofitException(exception)
        assertThat(cut.state.value).isEqualTo(
            MapScreenViewState(
                cameraPosition = LatLng(intent.latitude, intent.longitude),
                cameraZoom = intent.zoom,
                chargingStations = listOf(),
                isLoading = false,
                uiMessage = null,
                fetchError = expectedErrorMessage,
                bottomSheetInfoObject = null
            )
        )
    }

    @Test
    fun `should verify viewState when submitting ShowBottomSheetWithInfo intent`() {
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
            MapScreenViewState(
                cameraPosition = LatLng(0.0, 0.0),
                cameraZoom = 10f,
                chargingStations = list,
                isLoading = false,
                uiMessage = null,
                fetchError = null,
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
            MapScreenViewState(
                cameraPosition = LatLng(0.0, 0.0),
                cameraZoom = 10f,
                chargingStations = list,
                isLoading = false,
                uiMessage = null,
                fetchError = null,
                bottomSheetInfoObject = null
            )
        )
    }

}
