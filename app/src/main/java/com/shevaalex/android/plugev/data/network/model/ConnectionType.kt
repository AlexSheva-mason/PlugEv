package com.shevaalex.android.plugev.data.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ConnectionType(
    @SerializedName("FormalName") val formalName: String?,
    @SerializedName("Title") val title: String?
)
