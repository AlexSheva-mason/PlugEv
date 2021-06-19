package com.shevaalex.android.plugev.data

import com.shevaalex.android.plugev.data.openchargemap.network.model.*
import com.shevaalex.android.plugev.domain.openchargemap.model.ChargingStation
import com.shevaalex.android.plugev.presentation.mapscreen.MapScreenIntent

object DataFactory {

    fun getChargingStationDto(): ChargingStationNetworkDto {
        return ChargingStationNetworkDto(
            id = "id${getAddressInfo().latitude}",
            usageCost = "usageCost",
            addressInfo = getAddressInfo(),
            usageType = getUsageType(),
            statusType = getStatusType(),
            connections = getConnectionList(),
            nOfPoints = 2
        )
    }

    fun getChargingStationDomainModel(
        usageCost: String = "usageCost",
        addressTitle: String = "addressInfoTitle",
        addressLine1: String = "addressLine1",
        addressLine2: String = "addressLine2",
        town: String = "town",
        province: String = "province",
        postCode: String = "postCode",
        latitude: Double = 51.72215137119824,
        longitude: Double = 0.047445889881063685,
        usageTypeTitle: String = "usageTypeTitle"
    ): ChargingStation {
        return ChargingStation(
            id = "id$latitude",
            usageCost = usageCost,
            addressTitle = addressTitle,
            addressLine1 = addressLine1,
            addressLine2 = addressLine2,
            town = town,
            province = province,
            postCode = postCode,
            distanceMiles = "1.23",
            latitude = latitude,
            longitude = longitude,
            usageTypeTitle = usageTypeTitle,
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

    private fun getStatusType(isOperational: Boolean? = true): StatusType {
        return StatusType(
            title = "statusTypeTitle",
            isOperational = isOperational
        )
    }

    private fun getConnectionList(): List<ConnectionNetworkDto> {
        return listOf(
            getConnectionNetworkDto("1", 3.0, 1),
            getConnectionNetworkDto("2", 7.0, 1)
        )
    }

    fun getConnectionNetworkDto(
        typeNum: String = "type0",
        power: Double? = 1.0,
        quantity: Int = 0,
        powerLevelTitle: String? = "test power level",
        isOperational: Boolean? = true
    ) = ConnectionNetworkDto(
        connectionType = getConnectionType(typeNum),
        statusType = getStatusType(isOperational = isOperational),
        powerLevelId = 3,
        powerLevel = PowerLevel(powerLevelTitle),
        power = power,
        quantity = quantity
    )

    private fun getConnectionType(type: String): ConnectionType {
        return ConnectionType(
            formalName = "connectionType$type",
            title = "connectionTitle"
        )
    }

    fun getMapScreenIntentShowChargingStationsForCurrentMapPosition(
        zoom: Float = 10f,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        distance: Float = 10f
    )
            : MapScreenIntent.ShowChargingStationsForCurrentMapPosition {
        return MapScreenIntent.ShowChargingStationsForCurrentMapPosition(
            zoom = zoom,
            latitude = latitude,
            longitude = longitude,
            distance = distance
        )
    }

}
