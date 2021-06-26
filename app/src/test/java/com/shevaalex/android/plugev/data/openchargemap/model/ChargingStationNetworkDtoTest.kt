package com.shevaalex.android.plugev.data.openchargemap.model

import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.data.DataFactory
import com.shevaalex.android.plugev.data.openchargemap.network.model.toDomainModel
import org.junit.Test

class ChargingStationNetworkDtoTest {

    @Test
    fun `charging station dto should map to domain model`() {
        val chargeStationDto = DataFactory.getChargingStationDto()
        val expectedResult = DataFactory.getChargingStationDomainModel()
        assertThat(chargeStationDto.toDomainModel()).isEqualTo(expectedResult)
    }

}
