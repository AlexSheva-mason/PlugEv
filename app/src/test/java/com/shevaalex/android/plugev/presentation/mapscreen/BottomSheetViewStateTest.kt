package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.data.DataFactory.getChargingStationDomainModel
import com.shevaalex.android.plugev.data.DataFactory.getConnectionNetworkDto
import com.shevaalex.android.plugev.data.network.model.toDomainModel
import org.junit.Test

class BottomSheetViewStateTest {

    @Test
    fun `should set isOperational`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel()

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.isOperational).isEqualTo(chargingStation.isOperationalStatus)
    }

    @Test
    fun `should set isPublic true`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel(usageTypeTitle = "PUBLIC")

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.isPublic).isTrue()
    }

    @Test
    fun `should set isPublic false`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel(usageTypeTitle = "PRIVATE")

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.isPublic).isFalse()
    }

    @Test
    fun `should set title`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel()

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.title).isEqualTo(chargingStation.addressTitle)
    }

    @Test
    fun `should set address using fullAddress function no nulls`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel()
        val fullAddress = chargingStation.addressLine1.plus(", ") +
                chargingStation.addressLine2.plus(", ") +
                chargingStation.town.plus(", ") +
                chargingStation.province.plus(", ") +
                chargingStation.postCode

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.address).isEqualTo(fullAddress)
    }

    @Test
    fun `should set address using blanks except postcode`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel(
            addressTitle = "",
            addressLine1 = "",
            addressLine2 = "",
            town = "",
            province = "",
            postCode = "CB6 3NW",
        )

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.address).isEqualTo("CB6 3NW")
    }

    @Test
    fun `should set accessType`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel()

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.accessType).isEqualTo(chargingStation.usageTypeTitle)
    }

    @Test
    fun `should set usageCost if not blank`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel()

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.usageCost).isEqualTo(chargingStation.usageCost)
    }

    @Test
    fun `should set usageCost unknown if blank`() {
        //GIVEN
        val chargingStation = getChargingStationDomainModel(usageCost = "")

        //WHEN
        val cut = BottomSheetViewState(chargingStation)

        //THEN
        assertThat(cut.usageCost).isEqualTo("£ Usage cost unknown")
    }

    @Test
    fun `should set BsConnectionListItemState quantityText NA`() {
        //GIVEN
        val connection = getConnectionNetworkDto(quantity = 0).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.quantityText).isEqualTo("N/A")
    }

    @Test
    fun `should set BsConnectionListItemState quantityText`() {
        //GIVEN
        val connection = getConnectionNetworkDto(quantity = 10).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.quantityText).isEqualTo("10x")
    }

    @Test
    fun `should set BsConnectionListItemState connectionTitle`() {
        //GIVEN
        val connection = getConnectionNetworkDto().toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.connectionTitle).isEqualTo(connection.connectionTitle)
    }

    @Test
    fun `should set BsConnectionListItemState powerLevelTitle with null`() {
        //GIVEN
        val connection = getConnectionNetworkDto(powerLevelTitle = null).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.powerLevelTitle).isEqualTo("Power level unknown")
    }

    @Test
    fun `should set BsConnectionListItemState powerLevelTitle`() {
        //GIVEN
        val connection = getConnectionNetworkDto().toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.powerLevelTitle).isEqualTo(connection.powerLevelTitle)
    }

    @Test
    fun `should set BsConnectionListItemState isOperational`() {
        //GIVEN
        val connection = getConnectionNetworkDto().toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.isOperational).isEqualTo(connection.isOperationalStatus)
    }

    @Test
    fun `should set BsConnectionListItemState operationalText = Operational`() {
        //GIVEN
        val connection = getConnectionNetworkDto(isOperational = true).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.operationalText).isEqualTo("Operational")
    }

    @Test
    fun `should set BsConnectionListItemState operationalText = Not Operational`() {
        //GIVEN
        val connection = getConnectionNetworkDto(isOperational = false).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.operationalText).isEqualTo("Not Operational")
    }

    @Test
    fun `should set BsConnectionListItemState operationalText = Unknown`() {
        //GIVEN
        val connection = getConnectionNetworkDto(isOperational = null).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.operationalText).isEqualTo("Unknown")
    }

    @Test
    fun `should set BsConnectionListItemState power = question KW`() {
        //GIVEN
        val connection = getConnectionNetworkDto(power = null).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.power).isEqualTo("? KW")
    }

    @Test
    fun `should set BsConnectionListItemState power = exact KW`() {
        //GIVEN
        val connection = getConnectionNetworkDto(power = 111.0).toDomainModel()

        //WHEN
        val cut = BsConnectionListItemState(connection)

        //THEN
        assertThat(cut.power).isEqualTo("111.0 KW")
    }

}
