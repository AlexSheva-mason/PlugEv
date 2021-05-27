package com.shevaalex.android.plugev.data.network

import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.data.DataFactory
import com.shevaalex.android.plugev.domain.model.DataResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class RetrofitSafeCallTest {

    @ExperimentalCoroutinesApi
    @Test
    fun `if call successful function returns DataResultSuccess`() = runBlockingTest {
        val testData = DataFactory.getChargingStationDomainModel()
        val successCall = suspend {
            testData
        }
        val result = retrofitCall(successCall)
        assertThat(result).isEqualTo(DataResult.Success(testData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `if call throws HttpException function returns DataResultError`() = runBlockingTest {
        val testException = HttpException(
            Response.error<String>(500, "testException".toResponseBody(null))
        )
        val successCall = suspend {
            throw testException
        }
        val result = retrofitCall(successCall)
        assertThat(result).isEqualTo(DataResult.Error(testException))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `if call throws IOException function returns DataResultError`() = runBlockingTest {
        val testException = IOException("testException")
        val successCall = suspend {
            throw testException
        }
        val result = retrofitCall(successCall)
        assertThat(result).isEqualTo(DataResult.Error(testException))
    }

}
