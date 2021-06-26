package com.shevaalex.android.plugev.domain.postcode.model

import com.google.android.gms.maps.model.LatLng

sealed class PostCode(
    open val status: Int
) {

    data class PostCodeSuccess(
        override val status: Int,
        val postCodeName: String,
        val position: LatLng,
    ) : PostCode(status)

    data class PostCodeError(
        override val status: Int,
        val error: String
    ) : PostCode(status)

}
