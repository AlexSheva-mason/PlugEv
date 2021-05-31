package com.shevaalex.android.plugev.presentation.mapscreen

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.shevaalex.android.plugev.CoroutinesTestRule
import com.shevaalex.android.plugev.data.DataFactory.getChargingStationDomainModel
import com.shevaalex.android.plugev.service.googlemap.PlugEvClusterManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MarkerManagerKtTest {

    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    @MockK
    private lateinit var clusterManager: PlugEvClusterManager

    private val latLngBounds = LatLngBounds(
        LatLng(52.19746383142719, 0.11946366316764238),
        LatLng(52.21242185840669, 0.14867744990904308)
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { clusterManager.addItem(any()) } returns true
        every { clusterManager.removeItem(any()) } returns true
    }

    @Test
    fun `addItemToCollection does not add item if it's out of LatLngBounds`() {
        //algorithm's collection does not contain 1 item, but it's out of LatLngBounds
        every {
            clusterManager.algorithm.items
        } returns listOf(
            getChargingStationDomainModel(52.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(52.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(52.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(52.20332286606809, 0.13211238948885104),
        )
        val chargingStationList = listOf(
            getChargingStationDomainModel(52.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(52.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(52.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(52.20332286606809, 0.13211238948885104),
            getChargingStationDomainModel(52.202899622094066, 0.10845701204142827)
        )

        runBlocking {
            addItemsToCollection(
                chargingStationList = chargingStationList,
                latLngBounds = latLngBounds,
                evClusterManager = clusterManager
            )
        }

        verify(exactly = 0) {
            clusterManager.addItem(any())
        }
    }

    @Test
    fun `addItemToCollection adds item if it's within LatLngBounds and not in algorithm's collection`() {
        //algorithm's collection does not contain 1 item
        every {
            clusterManager.algorithm.items
        } returns listOf(
            getChargingStationDomainModel(52.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(52.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(52.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(52.20332286606809, 0.13211238948885104),
        )

        //last item should be added to algorithm's collection
        val itemToAdd = getChargingStationDomainModel(52.20312154620868, 0.13168523135162127)
        val chargingStationList = listOf(
            getChargingStationDomainModel(52.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(52.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(52.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(52.20332286606809, 0.13211238948885104),
            itemToAdd
        )

        runBlocking {
            addItemsToCollection(
                chargingStationList = chargingStationList,
                latLngBounds = latLngBounds,
                evClusterManager = clusterManager
            )
        }

        verify(atMost = 1) {
            clusterManager.addItem(any())
        }
        verify(exactly = 1) {
            clusterManager.addItem(itemToAdd)
        }
    }

    @Test
    fun `addItemToCollection does not add item if it's already in the algorithm's collection`() {
        //algorithm's collection already contains all items
        every {
            clusterManager.algorithm.items
        } returns listOf(
            getChargingStationDomainModel(52.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(52.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(52.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(52.20332286606809, 0.13211238948885104),
        )
        val chargingStationList = listOf(
            getChargingStationDomainModel(52.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(52.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(52.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(52.20332286606809, 0.13211238948885104),
        )

        runBlocking {
            addItemsToCollection(
                chargingStationList = chargingStationList,
                latLngBounds = latLngBounds,
                evClusterManager = clusterManager
            )
        }

        verify(exactly = 0) {
            clusterManager.addItem(any())
        }
    }

    @Test
    fun `removeItemFromCollection does not remove item if it's within LatLngBounds`() {
        every {
            clusterManager.algorithm.items
        } returns listOf(
            getChargingStationDomainModel(52.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(52.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(52.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(52.20332286606809, 0.13211238948885104),
        )

        runBlocking {
            removeItemFromCollection(
                latLngBounds = latLngBounds,
                evClusterManager = clusterManager
            )
        }

        verify(exactly = 0) {
            clusterManager.removeItem(any())
        }
    }

    @Test
    fun `removeItemFromCollection removes an item if it's out of LatLngBounds`() {
        every {
            clusterManager.algorithm.items
        } returns listOf(
            getChargingStationDomainModel(53.20771169949917, 0.13875083384775075),
            getChargingStationDomainModel(53.200584275800814, 0.13395156135782119),
            getChargingStationDomainModel(53.20759197890927, 0.1229459606575915),
            getChargingStationDomainModel(53.20332286606809, 0.13211238948885104),
        )

        runBlocking {
            removeItemFromCollection(
                latLngBounds = latLngBounds,
                evClusterManager = clusterManager
            )
        }

        verify(exactly = 1) {
            clusterManager.removeItem(
                getChargingStationDomainModel(53.20771169949917, 0.13875083384775075)
            )
            clusterManager.removeItem(
                getChargingStationDomainModel(53.200584275800814, 0.13395156135782119)
            )
            clusterManager.removeItem(
                getChargingStationDomainModel(53.20759197890927, 0.1229459606575915)
            )
            clusterManager.removeItem(
                getChargingStationDomainModel(53.20332286606809, 0.13211238948885104)
            )
        }
    }

}
