package de.uniks.ws2122.cc.teamA.model.util

import de.uniks.ws2122.cc.teamA.Constant

class SportMode() {

    private val timeOptions: Map<String, Int>
    private val metersOptions: Map<String, Int>
    private val stepsOptions: Map<String, Int>

    /** option -> the challenge target value **/
    init {

        timeOptions = mapOf("1 Minute" to 60,"5 Minutes" to 5*60,"10 Minutes" to 10*60, "30 Minutes" to 30*60, "60 Minutes" to 60*60)

        metersOptions = mapOf("100 Meters" to 100, "500 Meters" to 500,"1 Kilometer" to 1000, "2 Kilometers" to 2000, "5 Kilometers" to 5000)

        stepsOptions = mapOf("100 Steps" to 100, "200 Steps" to 200, "500 Steps" to 500, "1000 Steps" to 1000, "10.000 Steps" to 10000)
    }

    /** returns the value of the selected option **/
    fun getOptionValue(mode: String, option: String): Int {

        return when(mode) {

            Constant.TIME -> timeOptions[option]!!
            Constant.METERS -> metersOptions[option]!!
            Constant.STEPS -> stepsOptions[option]!!
            else -> 0
        }
    }

    /** returns all options of the selected mode **/
    fun getOptions(mode: String): ArrayList<String> {

        when(mode) {

            Constant.TIME -> return getTimeOptions()
            Constant.METERS -> return getMetersOptions()
            Constant.STEPS -> return getStepsOptions()
        }

        return ArrayList()
    }

    /** returns all time options **/
    private fun getTimeOptions(): ArrayList<String> {

        val options = ArrayList<String>()

        timeOptions.keys.forEach {

            options.add(it)
        }

        return options
    }

    /** returns all meter options **/
    private fun getMetersOptions(): ArrayList<String> {

        val options = ArrayList<String>()

        metersOptions.keys.forEach {

            options.add(it)
        }

        return options
    }

    /** returns all step options **/
    private fun getStepsOptions(): ArrayList<String> {

        val options = ArrayList<String>()

        stepsOptions.keys.forEach {

            options.add(it)
        }

        return options
    }
}