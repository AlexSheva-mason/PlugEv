package com.shevaalex.android.plugev.data

import com.shevaalex.android.plugev.data.network.model.*
import com.shevaalex.android.plugev.domain.model.ChargingStation

object DataFactory {

    fun getChargingStationDto(): ChargingStationNetworkDto {
        return ChargingStationNetworkDto(
            id = "id",
            usageCost = "usageCost",
            addressInfo = getAddressInfo(),
            usageType = getUsageType(),
            statusType = getStatusType(),
            connections = getConnectionList(),
            nOfPoints = 2
        )
    }

    fun getExpectedChargingStationDomainModel(): ChargingStation {
        return ChargingStation(
            id = "id",
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
            connections = getConnectionList().map { it.toDomainModel() },
            totalNumberOfPoints = 2
        )
    }

    private fun getAddressInfo(): AddressInfo {
        return AddressInfo(
            title = "addressInfoTitle",
            addressLine1 = "addressLine1",
            addressLine2 = "addressLine2",
            town = "town",
            province = "province",
            postCode = "postCode",
            distanceMiles = 1.234567890,
            latitude = 51.72215137119824,
            longitude = 0.047445889881063685
        )
    }

    private fun getUsageType(): UsageType {
        return UsageType(
            title = "usageTypeTitle",
            isPayAtLocation = true,
            isMembershipRequired = true
        )
    }

    private fun getStatusType(): StatusType {
        return StatusType(
            title = "statusTypeTitle",
            isOperational = true
        )
    }

    private fun getConnectionList(): List<ConnectionNetworkDto> {
        return listOf(
            getConnectionNetworkDto("1", 3.0, 1),
            getConnectionNetworkDto("2", 7.0, 1)
        )
    }

    fun getConnectionNetworkDto(typeNum: String, power: Double, quantity: Int) = ConnectionNetworkDto(
        connectionType = getConnectionType(typeNum),
        statusType = getStatusType(),
        power = power,
        quantity = quantity
    )

    private fun getConnectionType(type: String): ConnectionType {
        return ConnectionType(
            formalName = "connectionType$type",
            title = "connectionTitle"
        )
    }

}
