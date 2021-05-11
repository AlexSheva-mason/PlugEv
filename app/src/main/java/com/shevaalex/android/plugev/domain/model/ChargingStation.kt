package com.shevaalex.android.plugev.domain.model

import androidx.annotation.Keep

@Keep
data class ChargingStation(
    val id: String,
    val usageCost: String,
    val addressTitle: String,
    val addressLine1: String,
    val addressLine2: String,
    val town: String,
    val province: String,
    val postCode: String,
    val distanceMiles: String,
    val latitude: Double,
    val longitude: Double,
    val usageTypeTitle: String,
    val isPayAtLocation: Boolean?,
    val isMembershipRequired: Boolean?,
    val statusTypeTitle: String,
    val isOperationalStatus: Boolean,
    val connections: List<Connection>,
    val totalNumberOfPoints: Int
)

@Keep
data class Connection(
    val connectionFormalName: String,
    val connectionTitle: String,
    val statusTitle: String,
    val isOperationalStatus: Boolean?,
    val power: String,
    val quantity: Int,
)
