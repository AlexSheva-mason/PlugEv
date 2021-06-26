package com.shevaalex.android.plugev.domain.openchargemap.model

import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.shevaalex.android.plugev.presentation.mapscreen.truncate

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
    val isOperationalStatus: Boolean?,
    val connections: List<Connection>,
    val totalNumberOfPoints: Int
) : ClusterItem {

    override fun getPosition(): LatLng = LatLng(latitude, longitude)

    override fun getTitle(): String? {
        addressTitle.isNotBlank().also {
            return if (it) addressTitle.truncate(20)
            else null
        }
    }

    override fun getSnippet(): String? = null

}

@Keep
data class Connection(
    val connectionFormalName: String,
    val connectionTitle: String,
    val statusTitle: String,
    val isOperationalStatus: Boolean?,
    val powerLevel: Int,
    val powerLevelTitle: String?,
    val power: String,
    val quantity: Int,
)
