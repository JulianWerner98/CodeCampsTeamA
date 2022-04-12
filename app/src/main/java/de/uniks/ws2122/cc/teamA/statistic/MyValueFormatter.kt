package de.uniks.ws2122.cc.teamA.statistic

import com.github.mikephil.charting.formatter.ValueFormatter


class MyValueFormatter : ValueFormatter(){

    override fun getFormattedValue(value: Float): String {
        return "" + value.toInt()
    }
}