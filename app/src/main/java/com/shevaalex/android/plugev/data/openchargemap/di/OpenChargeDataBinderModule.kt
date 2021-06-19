package com.shevaalex.android.plugev.data.openchargemap.di

import com.shevaalex.android.plugev.data.openchargemap.ChargingStationRepositoryImpl
import com.shevaalex.android.plugev.domain.openchargemap.repository.ChargingStationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class OpenChargeDataBinderModule {

    @Binds
    @ViewModelScoped
    abstract fun bindChargingStationRepository(
        chargingStationRepositoryImpl: ChargingStationRepositoryImpl
    ): ChargingStationRepository

}
