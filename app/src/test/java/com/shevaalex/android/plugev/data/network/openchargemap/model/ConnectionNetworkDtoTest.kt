package com.shevaalex.android.plugev.data.network.openchargemap.model

import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.data.DataFactory
import com.shevaalex.android.plugev.domain.model.Connection
import org.junit.Test

class ConnectionNetworkDtoTest {

    @Test
    fun `connection dto should map to domain model`() {
        val connectionDto = DataFactory.getConnectionNetworkDto(
            typeNum = "testType",
            power = 7.53,
            quantity = 5
        )
        val expectedResult = Connection(
            connectionFormalName = "connectionTypetestType",
            connectionTitle = "connectionTitle",
            statusTitle = "statusTypeTitle",
            isOperationalStatus = true,
            powerLevel = 3,
            powerLevelTitle = "test power level",
            power = "7.53",
            quantity = 5
        )
        assertThat(connectionDto.toDomainModel()).isEqualTo(expectedResult)
    }

}
