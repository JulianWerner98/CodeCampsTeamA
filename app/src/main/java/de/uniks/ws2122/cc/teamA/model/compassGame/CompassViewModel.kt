package de.uniks.ws2122.cc.teamA.model.compassGame

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import de.uniks.ws2122.cc.teamA.CompassActivity
import de.uniks.ws2122.cc.teamA.Service.TimerService
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.repository.CompassRepository
import kotlin.math.atan2


class CompassViewModel : ViewModel() {
    var PERMISSION_ID: Int = 1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var location: Location? = null
    private var compassRepo: CompassRepository = CompassRepository()
    private var numberOfEmblems = 3

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorMagneticField: Sensor

    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)

    private val floatOrientation = FloatArray(3)
    private val floatOrientationArray = ArrayList<FloatArray>(1000)
    private val floatRotationMatrix = FloatArray(9)

    private lateinit var sensorCallback: (FloatArray) -> Unit
    private lateinit var getLocationCallback: () -> Unit

    var currentGame: CompassGame? = null
    var timerService: TimerService? = null

    fun getRandomLocation(compassActivity: CompassActivity, callback: (List<Feature>) -> Unit) {
        compassRepo.getApiObject(compassActivity, numberOfEmblems) {
            currentGame = CompassGame(ArrayList(it), null, null, null, null)
            callback.invoke(it)
        }
    }

    fun getAngleToLocation(
        compassActivity: CompassActivity,
        feature: Feature,
        callbackDegree: (Double) -> Unit
    ) {
        getLastLocation(compassActivity)
        getLocationCallback = {
            Log.d("Current Position", location!!.latitude.toString() + " " + location!!.longitude)
            val x = 9.490193682869808  //location!!.longitude
            val y = 51.31812310171641 //location!!.latitude
            val x2 = feature.geometry.coordinates[0]
            val y2 = feature.geometry.coordinates[1]
            val radian: Double = atan2(y2 - y, x2 - x)
            val degree = radian * 180 / Math.PI
            Log.d("Searched Angle", degree.toString())
            callbackDegree.invoke(degree)
        }
    }

    fun getLastLocation(compassActivity: CompassActivity) {
        if (checkPermission(compassActivity)) {
            if (isLocationEnabled(compassActivity)) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    location = task.result
                    if (location == null) {
                        newLocationData(compassActivity)
                    } else {
                        getLocationCallback.invoke()
                    }
                }
            } else {
                Toast.makeText(
                    compassActivity,
                    "Please Turn on Your device Location",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else {
            requestPermission(compassActivity)
        }
    }

    fun checkPermission(compassActivity: CompassActivity): Boolean {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(compassActivity)
        if (
            ActivityCompat.checkSelfPermission(
                compassActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                compassActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun requestPermission(compassActivity: CompassActivity) {
        ActivityCompat.requestPermissions(
            compassActivity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    fun isLocationEnabled(compassActivity: CompassActivity): Boolean {
        var locationManager: LocationManager =
            compassActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun newLocationData(compassActivity: CompassActivity) {
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(compassActivity)
        if (checkPermission(compassActivity)) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()!!
            )
        }

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            location = locationResult.lastLocation
            getLocationCallback.invoke()
        }
    }

    fun setupSensors(compassActivity: CompassActivity, sensorCallback: (FloatArray) -> Unit) {
        this.sensorCallback = sensorCallback
        sensorManager = compassActivity.getSystemService(SENSOR_SERVICE) as SensorManager

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        startSensor()

    }

    fun startSensor() {
        sensorManager.registerListener(
            sensorEventListenerAccelrometer,
            sensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        );
        sensorManager.registerListener(
            sensorEventListenerMagneticField,
            sensorMagneticField,
            SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    fun stopSensor() {
        sensorManager.unregisterListener(sensorEventListenerAccelrometer)
        sensorManager.unregisterListener(sensorEventListenerMagneticField);
    }

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
            floatOrientationArray.add(floatOrientation)
            if (floatOrientationArray.size > 1000) {
                floatOrientationArray.removeAt(0)
            }
            val map1 = floatOrientationArray.map { it -> it[0] }
            val map2 = floatOrientationArray.map { it -> it[0] }
            val map3 = floatOrientationArray.map { it -> it[0] }
            floatOrientation[0] = map1.average().toFloat()
            floatOrientation[1] = map2.average().toFloat()
            floatOrientation[2] = map3.average().toFloat()
            sensorCallback.invoke(floatOrientation)
            //imageView.rotation = (-floatOrientation[0] * 180 / Math.PI).toFloat() - 90F
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
            sensorCallback.invoke(floatOrientation)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    fun getGame(appViewModel: AppViewModel, callback: (CompassGame?) -> Unit)    {
        if (currentGame != null) {
            callback.invoke(currentGame)
        }
        compassRepo.getGame(appViewModel)  { game ->
            currentGame = game
            callback.invoke(game)
        }
    }

    fun createGame(
        compassActivity: CompassActivity,
        appViewModel: AppViewModel,
        callback: (CompassGame?) -> Unit
    ) {
        getRandomLocation(compassActivity) { emblems ->
            currentGame!!.players.add(appViewModel.getUID())
            compassRepo.createGame(currentGame) { game ->
                currentGame = game
                callback.invoke(game)
            }
        }
    }

    fun nextObject(
        compassActivity: CompassActivity,
        currentObjectCount: Int,
        callback: (Double) -> Unit
    ) {
        getAngleToLocation(compassActivity, currentGame!!.objectList[currentObjectCount], callback)
    }

    fun getRequest(appViewModel: AppViewModel,callback: (CompassGame?) -> Unit) {
        compassRepo.getRequest(appViewModel) {
            currentGame = it
            callback.invoke(it)
        }
    }

    fun setListenerToGame (callback: (CompassGame?) -> Unit) {
        compassRepo.setListenerToGame(currentGame?.id){
            if(it != null) {
                currentGame = it
                callback.invoke(it)
            }

        }
    }

    fun startTime(appViewModel: AppViewModel) {
        if (appViewModel.getUID() == currentGame!!.players[0]) {
            compassRepo.startTime(currentGame!!, "0")
        } else {
            compassRepo.startTime(currentGame!!, "1")
        }
    }

    fun surrender() {
    }

    fun endTime(appViewModel: AppViewModel) {
        if (appViewModel.getUID() == currentGame!!.players[0]) {
            compassRepo.endTime(currentGame!!, "0")
        } else {
            compassRepo.endTime(currentGame!!, "1")
        }
    }
}