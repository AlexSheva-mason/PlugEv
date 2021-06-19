package com.shevaalex.android.plugev.data.openchargemap

import com.shevaalex.android.plugev.data.openchargemap.network.model.toDomainModel
import com.shevaalex.android.plugev.data.openchargemap.network.service.ChargingStationRetrofitService
import com.shevaalex.android.plugev.domain.NetworkSafeCaller
import com.shevaalex.android.plugev.domain.openchargemap.model.ChargingStation
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.openchargemap.repository.ChargingStationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import java.lang.Exception
import javax.inject.Inject

@ViewModelScoped
class ChargingStationRepositoryImpl
@Inject constructor(
    private val apiService: ChargingStationRetrofitService
) : ChargingStationRepository, NetworkSafeCaller<List<ChargingStation>> {

    override suspend fun getChargingStationsForLocationFiltered(
        latitude: Double,
        longitude: Double,
        distance: Float,
        levelIds: List<String>?,
        usageTypeIds: List<String>?
    ): DataResult<List<ChargingStation>> {
        return retrofitCall {
            apiService
                .getChargingStationsForLocationFiltered(
                    latitude = latitude,
                    longitude = longitude,
                    distance = distance,
                    levelIds = levelIds?.joinToString(separator = ","),
                    usageTypeIds = usageTypeIds?.joinToString(separator = ",")
                )
                .mapNotNull {
                    try {
                        it.toDomainModel()
                    } catch (ex: Exception) {
                        null
                    }
                }
        }
    }

}
