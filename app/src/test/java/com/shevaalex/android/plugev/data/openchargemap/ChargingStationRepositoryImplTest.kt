package com.shevaalex.android.plugev.data.openchargemap

import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.data.DataFactory
import com.shevaalex.android.plugev.data.openchargemap.network.model.toDomainModel
import com.shevaalex.android.plugev.data.openchargemap.network.service.ChargingStationRetrofitService
import com.shevaalex.android.plugev.domain.openchargemap.model.DataResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class ChargingStationRepositoryImplTest {

    private lateinit var cut: ChargingStationRepositoryImpl

    @MockK
    private lateinit var apiService: ChargingStationRetrofitService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        cut = ChargingStationRepositoryImpl(apiService)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultSuccess`() = runBlockingTest {
        //GIVEN
        val testData = DataFactory.getChargingStationDto()
        coEvery {
            apiService.getChargingStationsForLocationFiltered(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns listOf(testData)

        //WHEN
        val result = cut.getChargingStationsForLocationFiltered(0.0, 0.0, 0f, null, null)

        //THEN
        assertThat(result).isEqualTo(DataResult.Success(listOf(testData.toDomainModel())))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultError if network call throws HttpException`() = runBlockingTest {
        //GIVEN
        val testException = HttpException(
            Response.error<String>(500, "testException".toResponseBody(null))
        )
        coEvery {
            apiService.getChargingStationsForLocationFiltered(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws (testException)

        //WHEN
        val result = cut.getChargingStationsForLocationFiltered(0.0, 0.0, 0f, null, null)

        //THEN
        assertThat(result).isEqualTo(DataResult.Error(testException))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultError if network call throws IOException`() = runBlockingTest {
        //GIVEN
        val testException = IOException("testException")
        coEvery {
            apiService.getChargingStationsForLocationFiltered(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws (testException)

        //WHEN
        val result = cut.getChargingStationsForLocationFiltered(0.0, 0.0, 0f, null, null)

        //THEN
        assertThat(result).isEqualTo(DataResult.Error(testException))
    }

}
