package com.shevaalex.android.plugev.domain.openchargemap.model

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val e: Throwable) : DataResult<Nothing>()
}
