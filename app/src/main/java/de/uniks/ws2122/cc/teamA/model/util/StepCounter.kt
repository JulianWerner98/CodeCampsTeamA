package de.uniks.ws2122.cc.teamA.model.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast

class StepCounter(private val context: Context) : SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var running = false
    private var totalSteps = .0f
    private var previousTotalSteps = .0f
    private lateinit var callback: (steps: Int) -> Unit

    fun startSteps(callback: (steps: Int) -> Unit) {

        this.callback = callback

        loadSavedSteps()

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {

            Toast.makeText(context, "No step sensor detected", Toast.LENGTH_SHORT).show()
        }
        else {

            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        running = true
        Log.d("STEP", "running: $running")

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (running) {

            totalSteps = event!!.values[0]

            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            Log.d("STEP", currentSteps.toString())
            callback.invoke(currentSteps)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    fun resetSteps() {

        previousTotalSteps = totalSteps
        saveStepsLocal()
    }

    private fun saveStepsLocal() {

        val editor = context.getSharedPreferences(SHAREDPREF_STEPS, Context.MODE_PRIVATE).edit()
        editor.putFloat(STEPS_KEY, previousTotalSteps).apply()
    }

    private fun loadSavedSteps() {

        val sharedPreferences = context.getSharedPreferences(SHAREDPREF_STEPS, Context.MODE_PRIVATE)
        val savedSteps = sharedPreferences.getFloat(STEPS_KEY, .0f)
        Log.d("STEP", "Saved Steps: $savedSteps")
        previousTotalSteps = savedSteps
    }

    companion object {

        const val SHAREDPREF_STEPS = "SharedPrefSteps"
        const val STEPS_KEY = "StepsKey"
    }
}