package com.shevaalex.android.plugev.data.postcodesio.di

import com.shevaalex.android.plugev.data.postcodesio.network.service.PostcodeIoRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

private const val BASE_URL = "https://api.postcodes.io/"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PostCodeRetrofitBuilder

@Module
@InstallIn(SingletonComponent::class)
object PostCodeDataModule {

    @PostCodeRetrofitBuilder
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
    fun providePostCodeRetrofitService(
        @PostCodeRetrofitBuilder builder: Retrofit.Builder
    ): PostcodeIoRetrofitService {
        return builder
            .build()
            .create(PostcodeIoRetrofitService::class.java)
    }

}
