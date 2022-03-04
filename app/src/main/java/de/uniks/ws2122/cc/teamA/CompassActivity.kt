package de.uniks.ws2122.cc.teamA

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant.WAITINGFOROPPONENT
import de.uniks.ws2122.cc.teamA.Service.TimerService
import de.uniks.ws2122.cc.teamA.databinding.ActivityCompassBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.CompassViewModel


class CompassActivity : AppCompatActivity() {
    private var timer: Int = 0
    private lateinit var appViewModel: AppViewModel
    private lateinit var viewModel: CompassViewModel
    private lateinit var binding: ActivityCompassBinding
    private lateinit var imageView: ImageView
    private lateinit var objectLabel: TextView
    private lateinit var background: ConstraintLayout
    private var angle: Double = 0F.toDouble()
    private var searchedDegree: Double = 0F.toDouble()
    private var currentObjectCount: Int = 0
    private var firstDetection: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = binding.arrow
        objectLabel = binding.objectTV
        background = binding.background

        //ViewModel
        viewModel = ViewModelProvider(this)[CompassViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        //Setup Timer/Sensor
        viewModel.timerService = TimerService().setupTimer(this) { newTimerValue(it.toInt()) }
        viewModel.setupSensors(this) { newSensorValue(it) }

        binding.btnStart.isVisible = false
        binding.arrow.isVisible = false

        viewModel.getGame(appViewModel) { game ->
            if (game == null) {
                viewModel.getRequest() { game ->
                    viewModel.createGame(this, appViewModel) {
                        binding.btnStart.isVisible = true
                        binding.spinner.isVisible = false
                        binding.arrow.isVisible = true
                        binding.btnStart.setOnClickListener { startGame() }
                    }
                }

            } else {
                if (game.players.size < 2) {
                    objectLabel.text = WAITINGFOROPPONENT
                } else {
                    binding.btnStart.isVisible = true
                    binding.spinner.isVisible = false
                    binding.arrow.isVisible = true
                    binding.btnStart.setOnClickListener { startGame() }
                }
            }
        }


    }

    private fun startGame() {
        binding.btnStart.isVisible = false
        binding.spinner.isVisible = true
        binding.arrow.isVisible = false
        viewModel.nextObject(this, currentObjectCount) { next ->
            binding.spinner.isVisible = false
            binding.arrow.isVisible = true
            searchedDegree = next
            objectLabel.text = viewModel.currentGame!!.objectList[currentObjectCount].properties.Objekt
            viewModel.timerService?.resetTimer(this)
            viewModel.timerService?.startTimer(this)
        }
    }

    override fun finish() {
        viewModel.timerService?.resetTimer(this)
        viewModel.stopSensor()
        super.finish()
    }

    override fun onDestroy() {
        Log.d("Closed", "Closed")
        super.onDestroy()
    }


    fun newSensorValue(floatOrientation: FloatArray) {
        if (searchedDegree != 0F.toDouble()) {
            angle = floatOrientation[0] * 180 / Math.PI
            Log.d(
                "Debug",
                "Aktuell:" + angle + " Gesucht:" + searchedDegree + " Erste:" + firstDetection
            )
            if (searchedDegree + 15 >= angle && searchedDegree - 15 <= angle) {
                if (firstDetection == 0) {
                    firstDetection = timer
                } else if (timer - firstDetection in 2..5) {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(500)

                        viewModel.timerService?.stopTimer(this)
                        viewModel.stopSensor()
                        if (++currentObjectCount < viewModel.currentGame!!.objectList.size) {
                            viewModel.nextObject(this, currentObjectCount) { next ->
                                searchedDegree = next
                                objectLabel.text =
                                    viewModel.currentGame!!.objectList[currentObjectCount].properties.Objekt
                                viewModel.startSensor()
                                viewModel.timerService?.startTimer(this)
                            }
                        } else {
                            objectLabel.text = "Finish"
                            background.setBackgroundColor(Color.RED)
                        }
                    }
                    background.setBackgroundColor(Color.GREEN)
                }
            } else {
                firstDetection = 0
                background.setBackgroundColor(Color.parseColor("#393E46"))
            }
        }
    }

    fun newTimerValue(timer: Int) {
        this.timer = timer
        binding.time.text = timer.toString() + " sek"
    }


}