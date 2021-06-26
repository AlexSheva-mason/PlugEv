package com.shevaalex.android.plugev.data.postcodesio

import com.google.gson.Gson
import com.shevaalex.android.plugev.data.postcodesio.network.model.toDomainModel
import com.shevaalex.android.plugev.data.postcodesio.network.service.PostcodeIoRetrofitService
import com.shevaalex.android.plugev.data.common.network.NetworkSafeCaller
import com.shevaalex.android.plugev.data.postcodesio.network.model.PostcodeNetworkDto
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import com.shevaalex.android.plugev.domain.postcode.model.PostCode
import com.shevaalex.android.plugev.domain.postcode.repository.PostCodeRepository
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
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

    override suspend fun retrofitCall(apiCall: suspend () -> PostCode): DataResult<PostCode> {
        return try {
            DataResult.Success(apiCall.invoke())
        } catch (ex: HttpException) {
            ex.response()?.errorBody()?.let { errorResponse ->
                val errorObject =
                    try {
                        Gson().fromJson(errorResponse.charStream(), PostcodeNetworkDto::class.java)
                    } catch (e: Exception) {
                        PostcodeNetworkDto(
                            status = ex.code(),
                            postCode = null,
                            error = "Unexpected server response"
                        )
                    }
                DataResult.Success(errorObject.toDomainModel())
            } ?: DataResult.Error(ex)
        } catch (ex: IOException) {
            DataResult.Error(ex)
        }
    }

}
