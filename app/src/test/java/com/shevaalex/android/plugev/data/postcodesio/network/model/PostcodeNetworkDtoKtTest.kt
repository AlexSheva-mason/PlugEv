package com.shevaalex.android.plugev.data.postcodesio.network.model

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.plugev.domain.postcode.model.PostCode
import org.junit.Test

class PostcodeNetworkDtoKtTest {

    @Test
    fun `should map to domain model success`() {
        //GIVEN
        val dtoModel = PostcodeNetworkDto(
            status = 200,
            postCode = PostCodeResult(
                postCodeName = "CB6 3NW",
                longitude = 0.0,
                latitude = 0.0
            ),
            error = null
        )

        //WHEN
        val result = dtoModel.toDomainModel()

        //THEN
        assertThat(result).isEqualTo(
            PostCode.PostCodeSuccess(
                status = 200,
                postCodeName = "CB6 3NW",
                position = LatLng(0.0, 0.0)
            )
        )
    }

    @Test
    fun `should map to domain model error with message`() {
        //GIVEN
        val dtoModel = PostcodeNetworkDto(
            status = 400,
            postCode = null,
            error = "test error message"
        )

        //WHEN
        val result = dtoModel.toDomainModel()

        //THEN
        assertThat(result).isEqualTo(
            PostCode.PostCodeError(
                status = 400,
                error = "test error message"
            )
        )
    }

    @Test
    fun `should map to domain model error generic`() {
        //GIVEN
        val dtoModel = PostcodeNetworkDto(
            status = 400,
            postCode = null,
            error = null
        )

        //WHEN
        val result = dtoModel.toDomainModel()

        //THEN
        assertThat(result).isEqualTo(
            PostCode.PostCodeError(
                status = 400,
                error = "Invalid postcode"
            )
        )
    }

}
