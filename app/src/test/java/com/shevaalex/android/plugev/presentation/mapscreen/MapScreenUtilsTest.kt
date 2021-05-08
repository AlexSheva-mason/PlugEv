package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import com.google.maps.android.SphericalUtil
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class MapScreenUtilsTest {

    @Test
    fun computeDistanceMiles_converts_km_to_miles() {
        val latLngSet1 = LatLng(51.72215137119824, 0.047445889881063685)
        val latLngSet2 = LatLng(51.697272674253384, 0.7277835119867629)
        val distanceMeters = SphericalUtil.computeDistanceBetween(latLngSet1, latLngSet2)
        val distanceMiles = BigDecimal(distanceMeters.div(1609.344))
            .setScale(2, RoundingMode.HALF_EVEN)
            .toFloat()
        val result = computeDistanceMiles(latLngSet1, latLngSet2)
        assertThat(result).isEqualTo(distanceMiles)
    }

}