package com.shevaalex.android.plugev.domain.repository

import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.domain.model.DataResult

interface ChargingStationRepository {

    suspend fun getChargingStationsForLocation(
        latitude: Double,
        longitude: Double,
        distance: Float
    ): DataResult<List<ChargingStation>>

    suspend fun getChargingStationsForLocationFiltered(
        latitude: Double,
        longitude: Double,
        distance: Float,
        levelIds: List<String>?,
        usageTypeIds: List<String>?
    ): DataResult<List<ChargingStation>>

}
