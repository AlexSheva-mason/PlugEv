package com.shevaalex.android.plugev.data.postcodesio.network.service

import com.shevaalex.android.plugev.data.postcodesio.network.model.PostcodeNetworkDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PostcodeIoRetrofitService {

    @GET("/postcodes/{postcodeQuery}")
    suspend fun getPostCode(
        @Path("postcodeQuery") postcodeQuery: String
    ): PostcodeNetworkDto

}
