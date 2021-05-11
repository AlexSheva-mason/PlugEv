package com.shevaalex.android.plugev.data.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.plugev.domain.model.ChargingStation
import java.math.BigDecimal
import java.math.RoundingMode

@Keep
data class ChargingStationNetworkDto(
    @SerializedName("UUID") val id: String,
    @SerializedName("UsageCost") val usageCost: String?,
    @SerializedName("AddressInfo") val addressInfo: AddressInfo,
    @SerializedName("UsageType") val usageType: UsageType?,
    @SerializedName("StatusType") val statusType: StatusType,
    @SerializedName("Connections") val connections: List<ConnectionNetworkDto>,
    @SerializedName("NumberOfPoints") val nOfPoints: Int
)

fun ChargingStationNetworkDto.toDomainModel(): ChargingStation {
    return ChargingStation(
        id = this.id,
        usageCost = this.usageCost ?: "",
        addressTitle = this.addressInfo.title,
        addressLine1 = this.addressInfo.addressLine1 ?: "",
        addressLine2 = this.addressInfo.addressLine2 ?: "",
        town = this.addressInfo.town ?: "",
        province = this.addressInfo.province ?: "",
        postCode = this.addressInfo.postCode,
        distanceMiles = BigDecimal(this.addressInfo.distanceMiles)
            .setScale(2, RoundingMode.HALF_EVEN)
            .toString(),
        latitude = this.addressInfo.latitude,
        longitude = this.addressInfo.longitude,
        usageTypeTitle = this.usageType?.title ?: "",
        isPayAtLocation = this.usageType?.isPayAtLocation,
        isMembershipRequired = this.usageType?.isMembershipRequired,
        statusTypeTitle = this.statusType.title,
        isOperationalStatus = this.statusType.isOperational,
        connections = this.connections.map { it.toDomainModel() },
        totalNumberOfPoints = this.nOfPoints
    )
}
