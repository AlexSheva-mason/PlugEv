package com.shevaalex.android.plugev.data.postcodesio

import com.shevaalex.android.plugev.data.postcodesio.network.service.PostcodeIoRetrofitService
import com.shevaalex.android.plugev.domain.NetworkSafeCaller
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.postcode.model.PostCode
import com.shevaalex.android.plugev.domain.postcode.repository.PostCodeRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class PostCodeRepositoryImpl
@Inject constructor(
    private val apiService: PostcodeIoRetrofitService
) : PostCodeRepository, NetworkSafeCaller {

    override suspend fun getPostCodeLocation(postCodeQuery: String): DataResult<PostCode> {
        TODO("Not yet implemented")
    }

    override suspend fun <T> retrofitCall(apiCall: suspend () -> T): DataResult<T> {
        TODO("Not yet implemented")
    }

}
