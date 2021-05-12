package com.shevaalex.android.plugev.data

import com.shevaalex.android.plugev.data.network.model.toDomainModel
import com.shevaalex.android.plugev.data.network.retrofitCall
import com.shevaalex.android.plugev.data.network.service.ChargingStationRetrofitService
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.domain.model.DataResult
import com.shevaalex.android.plugev.domain.repository.ChargingStationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class ChargingStationRepositoryImpl
@Inject constructor(
    private val apiService: ChargingStationRetrofitService
) : ChargingStationRepository {

    override suspend fun getChargingStationsForLocation(
        latitude: Double,
        longitude: Double,
        distance: Float
    ): DataResult<List<ChargingStation>> {
        return retrofitCall {
            apiService.getChargingStationsForLocation(
                latitude = latitude,
                longitude = longitude,
                distance = distance
            ).map {
                it.toDomainModel()
            }
        }
    }

}
