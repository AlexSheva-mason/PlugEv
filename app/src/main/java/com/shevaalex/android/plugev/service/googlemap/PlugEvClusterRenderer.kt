package com.shevaalex.android.plugev.service.googlemap

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.shevaalex.android.plugev.R
import com.shevaalex.android.plugev.domain.openchargemap.model.ChargingStation
import com.shevaalex.android.plugev.presentation.common.compose.Teal800

class PlugEvClusterRenderer(
    private val context: Context,
    googleMap: GoogleMap,
    clusterManager: ClusterManager<ChargingStation>
) : DefaultClusterRenderer<ChargingStation>(context, googleMap, clusterManager) {

    override fun onBeforeClusterItemRendered(item: ChargingStation, markerOptions: MarkerOptions) {
        val iconDrawable = getIconDrawable(item)
        iconDrawable?.let { drawable ->
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawable.toBitmap()))
        }
        super.onBeforeClusterItemRendered(item, markerOptions)
    }

    override fun onClusterItemRendered(clusterItem: ChargingStation, marker: Marker) {
        marker.tag = clusterItem.id
        super.onClusterItemRendered(clusterItem, marker)
    }

    private fun getIconDrawable(item: ChargingStation): Drawable? {
        //get maximum power level among all connections
        val maxPowerLevel = if (item.connections.isNotEmpty()) {
            item.connections.maxOf {
                it.powerLevel
            }
        } else 1
        //get icon for a public charge point
        return if (item.usageTypeTitle.contains("public", true)) {
            when (item.isOperationalStatus) {
                //get icon for an operational charge point
                true -> {
                    //get icon depending on a power level
                    when (maxPowerLevel) {
                        2 -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_operational_lvl2,
                                null
                            )
                        }
                        3 -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_operational_lvl3,
                                null
                            )
                        }
                        else -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_operational_lvl1,
                                null
                            )
                        }
                    }
                }
                //get icon for non-operational charge point
                false -> {
                    //get icon depending on a power level
                    when (maxPowerLevel) {
                        2 -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_non_operational_lvl2,
                                null
                            )
                        }
                        3 -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_non_operational_lvl3,
                                null
                            )
                        }
                        else -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_non_operational_lvl1,
                                null
                            )
                        }
                    }
                }
                //get icon for an unknown status charge point
                null -> {
                    //get icon depending on a power level
                    when (maxPowerLevel) {
                        2 -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_unknown_lvl2,
                                null
                            )
                        }
                        3 -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_unknown_lvl3,
                                null
                            )
                        }
                        else -> {
                            ResourcesCompat.getDrawable(
                                context.resources,
                                R.drawable.ic_public_unknown_lvl1,
                                null
                            )
                        }
                    }
                }
            }
        }
        //get icon for a private charge point
        else {
            //get icon depending on a power level
            when (maxPowerLevel) {
                2 -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_private_lvl2,
                        null
                    )
                }
                3 -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_private_lvl3,
                        null
                    )
                }
                else -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_private_lvl1,
                        null
                    )
                }
            }
        }
    }

    override fun getColor(clusterSize: Int): Int {
        return Teal800.toArgb()
    }

}
