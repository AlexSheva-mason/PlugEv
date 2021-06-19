package com.shevaalex.android.plugev.data.network.openchargemap.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class StatusType(
    @SerializedName("Title") val title: String,
    @SerializedName("IsOperational") val isOperational: Boolean?
)
