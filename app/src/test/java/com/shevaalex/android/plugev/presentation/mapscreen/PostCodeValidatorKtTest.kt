package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class PostCodeValidatorKtTest {

    @Test
    fun `should return true if string length valid format` () {
        //GIVEN
        val postCodeString = "123456"

        //WHEN
        val result = validatePostCodeForRequest(postCode = postCodeString)

        //THEN
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false if string length too short` () {
        //GIVEN
        val postCodeString = "12345"

        //WHEN
        val result = validatePostCodeForRequest(postCode = postCodeString)

        //THEN
        assertThat(result).isFalse()
    }

}
