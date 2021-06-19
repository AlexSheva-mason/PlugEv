package com.shevaalex.android.plugev.data.network.openchargemap.service

import com.shevaalex.android.plugev.BuildConfig
import com.shevaalex.android.plugev.data.network.openchargemap.model.ChargingStationNetworkDto
import com.shevaalex.android.plugev.domain.openchargemap.API_RESULT_LIMIT
import retrofit2.http.GET
import retrofit2.http.Query

interface ChargingStationRetrofitService {

    @GET("/v3/poi/?key=${BuildConfig.OPEN_CHARGE_MAP_KEY}&opendata=true")
    suspend fun getChargingStationsForLocationFiltered(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distance") distance: Float,
        @Query("maxresults") maxResults: Int = API_RESULT_LIMIT,
        @Query("levelid") levelIds: String?,
        @Query("usagetypeid") usageTypeIds: String?
    ): List<ChargingStationNetworkDto>

}
