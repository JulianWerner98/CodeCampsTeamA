package de.uniks.ws2122.cc.teamA.model.SportChallenges

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SportChallengeViewModel() : ViewModel(), SensorEventListener {

    private val sportChallengeData: MutableLiveData<SportChallenge> = MutableLiveData()
    private lateinit var sensorManager: SensorManager
    private var running = false

    fun getSportChallengeData(): LiveData<SportChallenge> {

        return sportChallengeData
    }

    fun setSportChallengeData(value: SportChallenge) {

        sportChallengeData.value = value
    }

    fun countSteps(context: Context) {

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

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

        Log.d("STEP", "onSensorChanged")
        val data = sportChallengeData.value

        if (running) {

            data!!.userCountedSteps = event!!.values[0].toInt()
            setSportChallengeData(data)

            Log.d("STEP", event.values[0].toString())
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }
}