package com.shevaalex.android.plugev.data.network.model

import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.data.DataFactory
import org.junit.Test

class ChargingStationNetworkDtoTest {

    @Test
    fun `charging station dto should map to domain model`() {
        val chargeStationDto = DataFactory.getChargingStationDto()
        val expectedResult = DataFactory.getExpectedChargingStationDomainModel()
        assertThat(chargeStationDto.toDomainModel()).isEqualTo(expectedResult)
    }

}
