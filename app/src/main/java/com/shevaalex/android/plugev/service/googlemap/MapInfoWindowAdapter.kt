package com.shevaalex.android.plugev.service.googlemap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.material.textview.MaterialTextView
import com.shevaalex.android.plugev.R

class MapInfoWindowAdapter(private val layoutInflater: LayoutInflater) :
    GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun getInfoContents(p0: Marker): View? {
        val view = layoutInflater.inflate(R.layout.map_info_content, null)
        val tv = view.findViewById<MaterialTextView>(R.id.marker_title)
        tv.text = p0.title
        return view
    }

}
