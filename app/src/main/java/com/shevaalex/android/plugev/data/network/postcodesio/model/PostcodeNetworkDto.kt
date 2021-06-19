package com.shevaalex.android.plugev.data.network.postcodesio.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.plugev.domain.postcode.model.PostCode

data class PostcodeNetworkDto(
    val status: Int,
    @SerializedName("result") val postCode: PostCodeResult?,
    val error: String?,
)

fun PostcodeNetworkDto.toDomainModel(): PostCode {
    return if (this.postCode != null) {
        PostCode.PostCodeSuccess(
            status = this.status,
            postCodeName = postCode.postCodeName,
            position = LatLng(this.postCode.latitude, this.postCode.longitude)
        )
    } else {
        PostCode.PostCodeError(
            status = this.status,
            error = this.error ?: "Invalid postcode"
        )
    }
}
