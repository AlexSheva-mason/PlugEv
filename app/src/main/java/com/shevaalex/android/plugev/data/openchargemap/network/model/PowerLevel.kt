package com.shevaalex.android.plugev.data.openchargemap.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PowerLevel(
    @SerializedName("Title") val title: String?
)
