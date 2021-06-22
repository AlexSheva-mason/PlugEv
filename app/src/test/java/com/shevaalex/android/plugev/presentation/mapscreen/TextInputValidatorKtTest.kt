package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TextInputValidatorKtTest {

    @Test
    fun `should not return characters that are not within a-z 0-9 or whitespace`() {
        //GIVEN
        val input = "!£$%^_&*()A "

        //WHEN
        val result = returnValidatedTextForInput(input)

        //THEN
        assertThat(result).isEqualTo("A ")
    }

    @Test
    fun `should return all letters in uppercase`() {
        //GIVEN
        val input = "aBcdEfg"

        //WHEN
        val result = returnValidatedTextForInput(input)

        //THEN
        assertThat(result).isEqualTo("ABCDEFG")
    }

    @Test
    fun `should limit output to maximum 8 chars`() {
        //GIVEN
        val input = "ABCD EFGH"

        //WHEN
        val result = returnValidatedTextForInput(input)

        //THEN
        assertThat(result).isEqualTo("ABCD EFG")
    }

    @Test
    fun `should limit output whitespace to maximum 8 chars`() {
        //GIVEN
        val input = "          "

        //WHEN
        val result = returnValidatedTextForInput(input)

        //THEN
        assertThat(result).isEqualTo("        ")
    }

}
