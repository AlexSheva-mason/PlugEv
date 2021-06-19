package com.shevaalex.android.plugev.domain

import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult

interface NetworkSafeCaller {

    suspend fun <T> retrofitCall(apiCall: suspend () -> T): DataResult<T>

}
