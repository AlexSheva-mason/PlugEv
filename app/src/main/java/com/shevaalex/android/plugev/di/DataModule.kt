package com.shevaalex.android.plugev.di

import com.shevaalex.android.plugev.data.network.service.ChargingStationRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.openchargemap.io/v3/poi/"

@Module
@InstallIn(ViewModelComponent::class)
object DataModule {

    @Provides
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    fun provideChargingStationRetrofitService(builder: Retrofit.Builder): ChargingStationRetrofitService {
        return builder
            .build()
            .create(ChargingStationRetrofitService::class.java)
    }

}
