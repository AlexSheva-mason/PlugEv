package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TextInputValidatorKtTest {

    @Test
    fun `should not return characters that are not within a-z 0-9 or whitespace`(){
        //GIVEN
        val input = "!£$%^_ &*()Abc "

        //WHEN
        val result = returnValidatedTextForInput(input)

        //THEN
        assertThat(result).isEqualTo(" Abc ")
    }

}
