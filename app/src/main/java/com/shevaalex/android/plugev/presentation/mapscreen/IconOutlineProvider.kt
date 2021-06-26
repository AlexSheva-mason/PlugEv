package com.shevaalex.android.plugev.presentation.mapscreen

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class IconOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View?, outline: Outline?) {
        outline?.setOval(0, 0, view?.width ?: 150, view?.height ?: 150)
    }

}
