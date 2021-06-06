package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.data.DataFactory
import org.junit.Test

class MapScreenRequestValidatorTest {

    private val cut = MapScreenRequestValidator()

    @Test
    fun `should return false with distance less than 25m`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        val state = MapScreenViewState(chargingStations = list)
        val latitude = 51.50299
        val longitude = MAP_DEFAULT_LONGITUDE
        val zoom = MAP_DEFAULT_ZOOM

        //WHEN
        val result = cut.validateNewPositionForRequest(
            state = state,
            latitude = latitude,
            longitude = longitude,
            zoom = zoom
        )

        //THEN
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true with distance more than 25m`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        val state = MapScreenViewState(chargingStations = list)
        val latitude = 51.504
        val longitude = MAP_DEFAULT_LONGITUDE
        val zoom = MAP_DEFAULT_ZOOM

        //WHEN
        val result = cut.validateNewPositionForRequest(
            state = state,
            latitude = latitude,
            longitude = longitude,
            zoom = zoom
        )

        //THEN
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false if list of data is not empty`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        val state = MapScreenViewState(chargingStations = list)
        val latitude = MAP_DEFAULT_LATITUDE
        val longitude = MAP_DEFAULT_LONGITUDE
        val zoom = MAP_DEFAULT_ZOOM

        //WHEN
        val result = cut.validateNewPositionForRequest(
            state = state,
            latitude = latitude,
            longitude = longitude,
            zoom = zoom
        )

        //THEN
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true if list of data is empty`() {
        //GIVEN
        val state = MapScreenViewState()
        val latitude = MAP_DEFAULT_LATITUDE
        val longitude = MAP_DEFAULT_LONGITUDE
        val zoom = MAP_DEFAULT_ZOOM

        //WHEN
        val result = cut.validateNewPositionForRequest(
            state = state,
            latitude = latitude,
            longitude = longitude,
            zoom = zoom
        )

        //THEN
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true if zoom level increased`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        val state = MapScreenViewState(chargingStations = list)
        val latitude = MAP_DEFAULT_LATITUDE
        val longitude = MAP_DEFAULT_LONGITUDE
        val zoom = 20f

        //WHEN
        val result = cut.validateNewPositionForRequest(
            state = state,
            latitude = latitude,
            longitude = longitude,
            zoom = zoom
        )

        //THEN
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true when zoom level decreased`() {
        //GIVEN
        val list = listOf(DataFactory.getChargingStationDomainModel())
        val state = MapScreenViewState(chargingStations = list)
        val latitude = MAP_DEFAULT_LATITUDE
        val longitude = MAP_DEFAULT_LONGITUDE
        val zoom = 9f

        //WHEN
        val result = cut.validateNewPositionForRequest(
            state = state,
            latitude = latitude,
            longitude = longitude,
            zoom = zoom
        )

        //THEN
        assertThat(result).isTrue()
    }

}
