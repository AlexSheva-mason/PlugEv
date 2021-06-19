package com.shevaalex.android.plugev.data.network.postcodesio.model

import com.google.gson.annotations.SerializedName

data class PostcodeNetworkDto(
    val status: Int,
    @SerializedName("result") val postCode: PostCodeResult?,
    val error: String?,
)
