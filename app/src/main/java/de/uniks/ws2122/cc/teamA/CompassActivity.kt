package de.uniks.ws2122.cc.teamA

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivityCompassBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.CompassViewModel

class CompassActivity : AppCompatActivity(){
    private lateinit var appViewModel: AppViewModel
    private lateinit var viewModel: CompassViewModel
    private lateinit var binding: ActivityCompassBinding
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorMagneticField: Sensor

    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)

    private val floatOrientation = FloatArray(3)
    private val floatRotationMatrix = FloatArray(9)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = binding.arrow
        textView = binding.degree

        //ViewModel
        viewModel = ViewModelProvider(this)[CompassViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        //viewModel.getRandomLocation()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        imageView.rotation = 90F

        val sensorEventListenerAccelrometer: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                floatGravity = event.values
                SensorManager.getRotationMatrix(
                    floatRotationMatrix,
                    null,
                    floatGravity,
                    floatGeoMagnetic
                )
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation)
                imageView.rotation = (-floatOrientation[0] * 180 / 3.14159).toFloat() -90F
                textView.text = (-floatOrientation[0] * 180 / 3.14159).toString()
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        val sensorEventListenerMagneticField: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                floatGeoMagnetic = event.values
                SensorManager.getRotationMatrix(
                    floatRotationMatrix,
                    null,
                    floatGravity,
                    floatGeoMagnetic
                )
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation)
                imageView.rotation = (-floatOrientation[0] * 180 / 3.14159).toFloat() -90F
                textView.text = (-floatOrientation[0] * 180 / 3.14159).toString()
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        sensorManager.registerListener(sensorEventListenerAccelrometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }
}