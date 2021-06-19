package com.shevaalex.android.plugev.data.network

import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import retrofit2.HttpException
import java.io.IOException


suspend fun <T> retrofitCall(apiCall: suspend () -> T): DataResult<T> {
    return try {
        DataResult.Success(apiCall.invoke())
    } catch (ex: HttpException) {
        DataResult.Error(ex)
    } catch (ex: IOException) {
        DataResult.Error(ex)
    }
}
