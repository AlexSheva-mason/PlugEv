package com.shevaalex.android.plugev.data.openchargemap.di

import com.shevaalex.android.plugev.data.openchargemap.network.service.ChargingStationRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

private const val BASE_URL = "https://api.openchargemap.io/v3/poi/"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChargingStationRetrofitBuilder

@Module
@InstallIn(SingletonComponent::class)
object OpenChargeDataModule {

    @ChargingStationRetrofitBuilder
    @Singleton
    @Provides
    fun provideRetrofitBuilder(client: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideChargingStationRetrofitService(
        @ChargingStationRetrofitBuilder builder: Retrofit.Builder
    ): ChargingStationRetrofitService {
        return builder
            .build()
            .create(ChargingStationRetrofitService::class.java)
    }

}
