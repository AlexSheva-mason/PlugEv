package com.shevaalex.android.plugev.data.postcodesio

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.shevaalex.android.plugev.data.DataFactory
import com.shevaalex.android.plugev.data.postcodesio.network.model.PostcodeNetworkDto
import com.shevaalex.android.plugev.data.postcodesio.network.model.toDomainModel
import com.shevaalex.android.plugev.data.postcodesio.network.service.PostcodeIoRetrofitService
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


class PostCodeRepositoryImplTest {

    private lateinit var cut: PostCodeRepositoryImpl

    @MockK
    private lateinit var apiService: PostcodeIoRetrofitService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        cut = PostCodeRepositoryImpl(apiService = apiService)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultSuccess`() = runBlockingTest {
        //GIVEN
        val data = DataFactory.getPostCodeDto()
        coEvery { apiService.getPostCode(any()) } returns data

        //WHEN
        val result = cut.getPostCodeLocation("")

        //THEN
        assertThat(result).isEqualTo(DataResult.Success(data.toDomainModel()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultError if network call throws HttpException null error body`() =
        runBlockingTest {
            //GIVEN
            val testException = HttpException(
                Response.success("test response")
            )
            coEvery { apiService.getPostCode(any()) } throws testException

            //WHEN
            val result = cut.getPostCodeLocation("")

            //THEN
            assertThat(result).isEqualTo(DataResult.Error(testException))
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultSuccess with a generic error if network call throws HttpException`() =
        runBlockingTest {
            //GIVEN
            val testException = HttpException(
                Response.error<String>(500, "testException".toResponseBody(null))
            )
            coEvery { apiService.getPostCode(any()) } throws testException

            //WHEN
            val result = cut.getPostCodeLocation("")

            //THEN
            val expected = PostcodeNetworkDto(
                status = testException.code(),
                postCode = null,
                error = "Unexpected server response"
            ).toDomainModel()
            assertThat(result).isEqualTo(DataResult.Success(expected))
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultSuccess with PostCode Error if network call throws HttpException`() =
        runBlockingTest {
            //GIVEN
            val errorPostcodeDto = PostcodeNetworkDto(
                status = 404,
                postCode = null,
                error = "Invalid postcode"
            )
            val statusJson = Gson().toJson(errorPostcodeDto)
            val testException = HttpException(
                Response.error<String>(404, statusJson.toResponseBody(null))
            )
            coEvery { apiService.getPostCode(any()) } throws testException

            //WHEN
            val result = cut.getPostCodeLocation("")

            //THEN
            val expected = errorPostcodeDto.toDomainModel()
            assertThat(result).isEqualTo(DataResult.Success(expected))
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return DataResultError if network call throws IOException`() = runBlockingTest {
        //GIVEN
        val testException = IOException("testException")
        coEvery { apiService.getPostCode(any()) } throws testException

        //WHEN
        val result = cut.getPostCodeLocation("")

        //THEN
        assertThat(result).isEqualTo(DataResult.Error(testException))
    }

}
