package com.shevaalex.android.plugev.data.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.plugev.domain.model.Connection

@Keep
data class ConnectionNetworkDto(
    @SerializedName("ConnectionType") val connectionType: ConnectionType?,
    @SerializedName("StatusType") val statusType: StatusType?,
    @SerializedName("LevelID") val powerLevel: Int?,
    @SerializedName("PowerKW") val power: Double?,
    @SerializedName("Quantity") val quantity: Int,
)

fun ConnectionNetworkDto.toDomainModel(): Connection {
    return Connection(
        connectionFormalName = this.connectionType?.formalName ?: "",
        connectionTitle = this.connectionType?.title ?: "",
        statusTitle = this.statusType?.title ?: "",
        isOperationalStatus = this.statusType?.isOperational,
        powerLevel = this.powerLevel ?: 1,
        power = this.power?.toString() ?: "",
        quantity = this.quantity
    )
}
