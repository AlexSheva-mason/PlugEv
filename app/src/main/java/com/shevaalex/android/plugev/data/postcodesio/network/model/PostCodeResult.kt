package com.shevaalex.android.plugev.data.postcodesio.network.model

import com.google.gson.annotations.SerializedName

data class PostCodeResult(
    @SerializedName("postcode") val postCodeName: String,
    val longitude: Double?,
    val latitude: Double?,
)
