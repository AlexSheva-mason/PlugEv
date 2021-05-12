package com.shevaalex.android.plugev.data.network.service

import com.shevaalex.android.plugev.BuildConfig
import com.shevaalex.android.plugev.data.network.model.ChargingStationNetworkDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ChargingStationRetrofitService {

    @GET("/v3/poi/?key=${BuildConfig.OPEN_CHARGE_MAP_KEY}&opendata=true")
    suspend fun getChargingStationsForLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distance") distance: Float,
        @Query("maxresults") maxResults: Int = 250
    ): List<ChargingStationNetworkDto>

}
