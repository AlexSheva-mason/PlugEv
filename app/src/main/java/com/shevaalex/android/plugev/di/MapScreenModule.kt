package com.shevaalex.android.plugev.di

import com.shevaalex.android.plugev.data.network.openchargemap.ChargingStationRepositoryImpl
import com.shevaalex.android.plugev.domain.repository.ChargingStationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class MapScreenModule {

    @Binds
    @ViewModelScoped
    abstract fun bindChargingStationRepository(
        chargingStationRepositoryImpl: ChargingStationRepositoryImpl
    ): ChargingStationRepository

}
