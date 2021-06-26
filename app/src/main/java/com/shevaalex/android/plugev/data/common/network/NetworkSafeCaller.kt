package com.shevaalex.android.plugev.data.common.network

import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult

interface NetworkSafeCaller<T> {

    suspend fun retrofitCall(apiCall: suspend () -> T): DataResult<T>

}
