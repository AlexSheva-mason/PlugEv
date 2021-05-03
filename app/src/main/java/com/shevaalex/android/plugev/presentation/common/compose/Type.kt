package com.shevaalex.android.plugev.presentation.common.compose

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shevaalex.android.plugev.R

private val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.W400),
    Font(R.font.montserrat_medium, FontWeight.W500),
)

private val SourceSansPro = FontFamily(
    Font(R.font.sourcesanspro_light, FontWeight.W300),
    Font(R.font.sourcesanspro_regular, FontWeight.W400),
    Font(R.font.sourcesanspro_semibold, FontWeight.W500)
)

val PlugEvTypography = Typography(
    h1 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W300,
        fontSize = 104.sp
    ),
    h2 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W300,
        fontSize = 65.sp
    ),
    h3 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W400,
        fontSize = 52.sp
    ),
    h4 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W400,
        fontSize = 37.sp
    ),
    h5 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W400,
        fontSize = 26.sp
    ),
    h6 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W500,
        fontSize = 22.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W400,
        fontSize = 17.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = SourceSansPro,
        fontWeight = FontWeight.W500,
        fontSize = 15.sp
    ),
    body1 = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.W400,
        fontSize = 10.sp
    )
)
