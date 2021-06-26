package com.shevaalex.android.plugev.domain.openchargemap.usecase

import com.shevaalex.android.plugev.domain.openchargemap.model.ChargingStation
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.openchargemap.repository.ChargingStationRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetChargeStationListUseCase
@Inject constructor(
    private val chargeStationRepository: ChargingStationRepository
) {

    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        distance: Float,
        levelIds: List<String>? = null,
        usageTypeIds: List<String>? = null
    ): DataResult<List<ChargingStation>> {
        return chargeStationRepository.getChargingStationsForLocationFiltered(
            latitude = latitude,
            longitude = longitude,
            distance = distance,
            levelIds = levelIds,
            usageTypeIds = usageTypeIds
        )
    }

}
