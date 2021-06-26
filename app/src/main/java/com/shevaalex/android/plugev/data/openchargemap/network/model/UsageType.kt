package com.shevaalex.android.plugev.data.openchargemap.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UsageType(
    @SerializedName("Title") val title: String,
    @SerializedName("IsPayAtLocation") val isPayAtLocation: Boolean,
    @SerializedName("IsMembershipRequired") val isMembershipRequired: Boolean
)
