package com.shevaalex.android.plugev.domain

import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import retrofit2.HttpException
import java.io.IOException

interface NetworkSafeCaller<T> {

    suspend fun retrofitCall(apiCall: suspend () -> T): DataResult<T> {
        return try {
            DataResult.Success(apiCall.invoke())
        } catch (ex: HttpException) {
            DataResult.Error(ex)
        } catch (ex: IOException) {
            DataResult.Error(ex)
        }
    }

}
