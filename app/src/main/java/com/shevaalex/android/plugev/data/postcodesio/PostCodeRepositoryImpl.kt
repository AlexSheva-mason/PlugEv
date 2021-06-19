package com.shevaalex.android.plugev.data.postcodesio

import com.shevaalex.android.plugev.data.postcodesio.network.model.toDomainModel
import com.shevaalex.android.plugev.data.postcodesio.network.service.PostcodeIoRetrofitService
import com.shevaalex.android.plugev.data.common.network.NetworkSafeCaller
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.postcode.model.PostCode
import com.shevaalex.android.plugev.domain.postcode.repository.PostCodeRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class PostCodeRepositoryImpl
@Inject constructor(
    private val apiService: PostcodeIoRetrofitService
) : PostCodeRepository, NetworkSafeCaller<PostCode> {

    override suspend fun getPostCodeLocation(postCodeQuery: String): DataResult<PostCode> {
        return retrofitCall {
            apiService
                .getPostCode(postcodeQuery = postCodeQuery)
                .toDomainModel()
        }
    }

}
