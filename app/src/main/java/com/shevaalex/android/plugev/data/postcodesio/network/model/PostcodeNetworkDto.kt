package com.shevaalex.android.plugev.data.postcodesio.network.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.plugev.domain.postcode.model.PostCode

data class PostcodeNetworkDto(
    val status: Int,
    @SerializedName("result") val postCode: PostCodeResult?,
    val error: String?,
)

fun PostcodeNetworkDto.toDomainModel(): PostCode {
    return this.postCode?.let {
        if (it.latitude == null || it.longitude == null) {
            PostCode.PostCodeError(
                status = this.status,
                error = this.error ?: "Geolocation for this postcode is not available"
            )
        } else {
            PostCode.PostCodeSuccess(
                status = this.status,
                postCodeName = postCode.postCodeName,
                position = LatLng(it.latitude, it.longitude)
            )
        }
    } ?: PostCode.PostCodeError(
        status = this.status,
        error = this.error ?: "Invalid postcode"
    )
}
