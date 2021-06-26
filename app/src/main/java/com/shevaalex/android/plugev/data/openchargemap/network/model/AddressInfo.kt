package com.shevaalex.android.plugev.data.openchargemap.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddressInfo(
    @SerializedName("Title") val title: String,
    @SerializedName("AddressLine1") val addressLine1: String?,
    @SerializedName("AddressLine2") val addressLine2: String?,
    @SerializedName("Town") val town: String?,
    @SerializedName("StateOrProvince") val province: String?,
    @SerializedName("Postcode") val postCode: String?,
    @SerializedName("Distance") val distanceMiles: Double,
    @SerializedName("Latitude") val latitude: Double,
    @SerializedName("Longitude") val longitude: Double
)
