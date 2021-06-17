package com.shevaalex.android.plugev.presentation.mapscreen

import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.domain.model.Connection

data class BottomSheetViewState(
    val chargingStation: ChargingStation,
) {

    val isOperational: Boolean? = chargingStation.isOperationalStatus

    val isPublic: Boolean = chargingStation.usageTypeTitle.contains("public", true)

    val title: String = chargingStation.addressTitle

    val address: String = fullAddress(chargingStation)

    val accessType: String = chargingStation.usageTypeTitle

    val usageCost: String = if (chargingStation.usageCost.isNotBlank()) {
        chargingStation.usageCost
    } else "£ Usage cost unknown"

    private val connections: List<Connection> = chargingStation.connections

    val connectionStateList: List<BsConnectionListItemState> = connections.map { connection ->
        BsConnectionListItemState(connection)
    }

    private fun fullAddress(chargingStation: ChargingStation): String {
        val addressLine1 = if (chargingStation.addressLine1.isNotBlank()) {
            chargingStation.addressLine1.plus(", ")
        } else null
        val addressLine2 = if (chargingStation.addressLine2.isNotBlank()) {
            chargingStation.addressLine2.plus(", ")
        } else null
        val town = if (chargingStation.town.isNotBlank()) {
            chargingStation.town.plus(", ")
        } else null
        val province = if (chargingStation.province.isNotBlank()) {
            chargingStation.province.plus(", ")
        } else null
        return addressLine1.orEmpty() +
                addressLine2.orEmpty() +
                town.orEmpty() +
                province.orEmpty() +
                chargingStation.postCode
    }

}

data class BsConnectionListItemState(
    val connection: Connection
) {

    val quantityText = if (connection.quantity == 0) {
        "N/A"
    } else connection.quantity.toString().plus("x")

    val connectionTitle = connection.connectionTitle

    val powerLevelTitle = connection.powerLevelTitle ?: "Power level unknown"

    val isOperational = connection.isOperationalStatus

    val operationalText = connection.isOperationalStatus?.let { isOperationalBoolean ->
        if (isOperationalBoolean) "Operational"
        else "Not Operational"
    } ?: "Unknown"

    val power = if (connection.power.isBlank()) "? KW" else connection.power.plus(" KW")

}
