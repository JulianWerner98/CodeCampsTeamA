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
    private var totalCount: Int = 0
    private lateinit var callback: (steps: Int) -> Unit

    fun startSteps(callback: (steps: Int) -> Unit) {

        this.callback = callback
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {

            Toast.makeText(context, "No sensor detected", Toast.LENGTH_SHORT).show()
        }
        else {

            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        running = true
        Log.d("STEP", "running: $running")

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (running) {

            totalCount = event!!.values[0].toInt()
            Log.d("STEP", event.values[0].toString())
            callback.invoke(totalCount)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}