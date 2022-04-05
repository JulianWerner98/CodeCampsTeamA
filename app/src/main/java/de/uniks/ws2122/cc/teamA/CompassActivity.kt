package de.uniks.ws2122.cc.teamA

import android.content.Context
import android.content.Intent
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
import de.uniks.ws2122.cc.teamA.Constant.LOSE
import de.uniks.ws2122.cc.teamA.Constant.READYTOSTART
import de.uniks.ws2122.cc.teamA.Constant.SURRENDER
import de.uniks.ws2122.cc.teamA.Constant.WAITINGFOROPPONENT
import de.uniks.ws2122.cc.teamA.Constant.WIN
import de.uniks.ws2122.cc.teamA.Service.TimerService
import de.uniks.ws2122.cc.teamA.databinding.ActivityCompassBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.compassGame.CompassGame
import de.uniks.ws2122.cc.teamA.model.compassGame.CompassViewModel


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
                viewModel.getRequest(appViewModel) { gameFromMatchRequest ->
                    if (gameFromMatchRequest == null) {
                        viewModel.createGame(this, appViewModel) {
                            viewModel.setListenerToGame() { gameChanged(it) }
                            gameChanged(it)
                        }
                    } else {
                        viewModel.setListenerToGame() { gameChanged(it) }
                        gameChanged(gameFromMatchRequest)
                    }
                }

            } else {
                viewModel.setListenerToGame() { gameChanged(it) }
                gameChanged(game)
            }
        }


    }

    override fun onBackPressed() {
        if (viewModel.currentGame!!.winner.isNotEmpty()) {
            exitGame()
        }
        super.onBackPressed()
    }

    private fun gameChanged(game: CompassGame?) {
        if (game == null) return
        var started: Boolean

        //Check Winner
        if (game.winner == appViewModel.getUID()) {
            viewModel.stopSensor()
            objectLabel.text = "You " + WIN
            binding.arrow.setImageResource(R.drawable.happy)
            binding.arrow.rotation = 0f

        } else if (game.winner.isNotEmpty()) {
            viewModel.stopSensor()
            binding.arrow.setImageResource(R.drawable.lame)
            objectLabel.text = "You " + LOSE
            binding.arrow.rotation = 0f
        }
        // Return if their is a winner
        if (game.winner.isNotEmpty()) {
            viewModel.timerService!!.stopTimer(this)
            binding.btnStart.isVisible = true
            binding.btnStart.text = "Exit Game"
            binding.btnStart.setOnClickListener() { exitGame() }
            binding.spinner.isVisible = false
            binding.arrow.isVisible = true
            binding.time.isVisible = false
            return
        }
        if (game.player0Endtime != null && game.player1Endtime != null) viewModel.checkWinner()
        if (appViewModel.getUID() == game.players[0]) {
            started = game.player0Starttime != null && game.player0Starttime!!.time != 0L
        } else {
            started = game.player1Starttime != null && game.player1Starttime!!.time != 0L
        }
        if (!started) {
            if (game.players.size < 2) {
                objectLabel.text = WAITINGFOROPPONENT
                binding.btnStart.isVisible = false
                binding.spinner.isVisible = true
                binding.arrow.isVisible = false
                binding.btnStart.setOnClickListener() {}
            } else {
                objectLabel.text = READYTOSTART
                binding.btnStart.isVisible = true
                binding.spinner.isVisible = false
                binding.arrow.isVisible = true
                binding.btnStart.setOnClickListener { startGame() }
            }
        } else {
            // Joined Again
            if (timer <= 0) {
                viewModel.surrender(appViewModel)
            }
        }

    }

    private fun exitGame() {
        viewModel.exitGame(appViewModel)
        val intent = Intent(this, GameSelectActivity::class.java).apply { }
        startActivity(intent)
    }

    private fun startGame() {
        timer = 1
        binding.btnStart.isVisible = false
        binding.spinner.isVisible = true
        binding.arrow.isVisible = false
        viewModel.nextObject(this, currentObjectCount) { next ->
            binding.spinner.isVisible = false
            binding.arrow.isVisible = true
            searchedDegree = next
            objectLabel.text =
                viewModel.currentGame!!.objectList[currentObjectCount].properties.Objekt
            viewModel.timerService?.resetTimer(this)
            viewModel.startTime(appViewModel)
            viewModel.timerService?.startTimer(this)
        }
    }

    override fun finish() {
        viewModel.timerService?.resetTimer(this)
        viewModel.stopSensor()
        super.finish()
    }

    fun newSensorValue(floatOrientation: FloatArray) {
        if (searchedDegree != 0F.toDouble()) {
            angle = floatOrientation[0] * 180 / Math.PI
            binding.arrow.rotation = angle.toFloat()
            Log.d(
                "Debug for Presentation",
                "Aktuell:" + angle + " Gesucht:" + searchedDegree + " Erste:" + firstDetection
            )
            if (searchedDegree + 15 >= angle && searchedDegree - 15 <= angle) {
                if (firstDetection == 0) {
                    firstDetection = timer
                } else if (timer - firstDetection in 2..5) {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(500)
                    }
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
                        //Finshed Game
                        viewModel.endTime(appViewModel)
                        background.setBackgroundColor(Color.parseColor("#393E46"))
                        binding.objectTV.text = "Finished Waiting for Opponent"
                        binding.arrow.isVisible = false

                        return
                    }
                    background.setBackgroundColor(Color.parseColor("#05930A"))
                }
            } else {
                firstDetection = 0
                background.setBackgroundColor(Color.parseColor("#393E46"))
            }
        }
    }

    fun newTimerValue(timer: Int) {
        this.timer = timer
        binding.time.text = timer.toString() + "sec"
        if (timer == 2) {
            binding.btnStart.setOnClickListener() { viewModel.surrender(appViewModel) }
            binding.btnStart.isVisible = true
            binding.btnStart.text = SURRENDER
        }
    }


}