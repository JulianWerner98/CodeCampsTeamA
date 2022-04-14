package de.uniks.ws2122.cc.teamA

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant.COMPASS_GAME
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
import de.uniks.ws2122.cc.teamA.model.util.Notifications


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
    private val notificationId = 123456
    private var friendId: String? = null
    private var inviteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Bind elements to variables
        imageView = binding.arrow
        objectLabel = binding.objectTV
        background = binding.background
        binding.curvedArrowLeft.isVisible = false
        binding.curvedArrowRight.isVisible = false

        //Get Intent Extra Variables
        friendId = intent.extras?.get(Constant.FRIENDID)?.toString()
        inviteId = intent.extras?.get(Constant.INVITEKEY)?.toString()
        if (friendId == "null") friendId = null
        if (inviteId == "null") inviteId = null

        //ViewModel
        viewModel = ViewModelProvider(this)[CompassViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        //Setup Timer/Sensor
        viewModel.timerService = TimerService().setupTimer(this) { newTimerValue(it.toInt()) }
        viewModel.setupSensors(this) { newSensorValue(it) }

        //Set visibility and listener
        binding.btnStart.isVisible = false
        binding.arrow.isVisible = false

        getOrCreateOrJoinGame()
    }

    private fun getOrCreateOrJoinGame() {
        //Try to get game
        viewModel.getGame(appViewModel) { game ->
            //There is no game
            if (game == null) {
                when {
                    // Join a private game
                    inviteId != null -> {
                        viewModel.joinGame(appViewModel, inviteId!!) { game ->
                            if (game != null) {
                                viewModel.setListenerToGame() { gameChanged(it) }
                                gameChanged(game)
                            }
                        }
                    }
                    //Create a private game
                    friendId == null -> {
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
                    }
                    //Create normal game
                    else -> {
                        viewModel.createPrivateGame(this, appViewModel, friendId!!) { privateGame ->
                            viewModel.setListenerToGame() { gameChanged(it) }
                            gameChanged(privateGame)
                        }
                    }
                }
                //there is a current game
            } else {
                // There is a invitation
                if (inviteId != null) {
                    //Can not join private game, already in a current game
                    if (game.players.size >= 2) {
                        Toast.makeText(
                            this,
                            "Not Joined! You are already in a game.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //Delete current game and join the private game
                        viewModel.deleteRequest {
                            viewModel.deleteGame(game, appViewModel.getUID()) {
                                viewModel.joinGame(appViewModel, inviteId!!) { game ->
                                    if (game != null) {
                                        viewModel.setListenerToGame() { gameChanged(it) }
                                        gameChanged(game)
                                    }
                                }
                            }
                        }
                    }

                } else {
                    //Want to create a privite game
                    if (friendId != null) {
                        //Can not create private game, already in a current game
                        if (game.players.size >= 2) {
                            Toast.makeText(
                                this,
                                "No Invite! You are already in a game",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            //Delete match query to make a private game
                            viewModel.deleteRequest {
                                viewModel.sendInvite(game!!.id!!, friendId!!, appViewModel.getUID())
                            }
                        }
                    }
                    viewModel.setListenerToGame() { gameChanged(it) }
                    gameChanged(game)
                }
            }
        }
    }

    /**
     * Override the onBackPressed Function
     * Used to save statics and history and delete private game
     * */
    override fun onBackPressed() {
        if (viewModel.currentGame!!.winner.isNotEmpty()) {
            exitGame()
        }
        if (friendId != null && viewModel.currentGame!!.players.size < 2) {
            viewModel.deleteGame(viewModel.currentGame!!, appViewModel.getUID()) {
                viewModel.deleteInvite(appViewModel.getUID(), friendId!!) {
                    Toast.makeText(this, "Delete Game and Request", Toast.LENGTH_SHORT).show()
                }
            }

        }
        super.onBackPressed()
    }

    private fun gameChanged(game: CompassGame?) {
        if (game == null) return
        //Setup for Notification
        var started: Boolean
        val intent = Intent(this, CompassGame::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notifications = Notifications()

        //Check Winner
        //Current User won
        if (game.winner == appViewModel.getUID()) {
            viewModel.stopSensor()
            objectLabel.text = "You " + WIN
            binding.arrow.setImageResource(R.drawable.happy)
            binding.arrow.rotation = 0f
            notifications.sendNotification(
                notificationId,
                COMPASS_GAME,
                "You won the game",
                this,
                pendingIntent
            )
            //Opponent won
        } else if (game.winner.isNotEmpty()) {
            viewModel.stopSensor()
            binding.arrow.setImageResource(R.drawable.lame)
            objectLabel.text = "You " + LOSE
            binding.arrow.rotation = 0f
            notifications.sendNotification(
                notificationId,
                COMPASS_GAME,
                "You lost the game",
                this,
                pendingIntent
            )
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
        //Check winner
        if (game.player0Endtime != null && game.player1Endtime != null) viewModel.checkWinner()

        //Check if the game is started
        started = if (appViewModel.getUID() == game.players[0]) {
            game.player0Starttime != null && game.player0Starttime!!.time != 0L
        } else {
            game.player1Starttime != null && game.player1Starttime!!.time != 0L
        }
        //Game not started
        if (!started) {
            if (game.players.size < 2) {
                objectLabel.text = WAITINGFOROPPONENT
                binding.btnStart.isVisible = false
                binding.spinner.isVisible = true
                binding.arrow.isVisible = false
                binding.btnStart.setOnClickListener() {}
            } else {
                notifications.sendNotification(
                    notificationId,
                    COMPASS_GAME,
                    "Opponent found",
                    this,
                    pendingIntent
                )
                objectLabel.text = READYTOSTART
                binding.btnStart.isVisible = true
                binding.spinner.isVisible = false
                binding.arrow.isVisible = true
                binding.btnStart.setOnClickListener { startGame() }
            }
        } else {
            // Joined Again -> Surrender Game
            if (timer <= 0) {
                viewModel.surrender(appViewModel)
            }
        }

    }

    /** Save statics and history
     *  Change to Game Select Screen
     * */
    private fun exitGame() {
        viewModel.exitGame(appViewModel)
        val intent = Intent(this, GameSelectActivity::class.java).apply { }
        startActivity(intent)
    }

    /** Start game  **/
    private fun startGame() {
        timer = 1
        // Set visibilities
        binding.btnStart.isVisible = false
        binding.spinner.isVisible = true
        binding.arrow.isVisible = false
        //Get first searched Object
        viewModel.nextObject(this, currentObjectCount) { next ->
            binding.spinner.isVisible = false
            binding.arrow.isVisible = true
            searchedDegree = next
            objectLabel.text =
                viewModel.currentGame!!.objectList[currentObjectCount].properties.Objekt
            //Start timer
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

    private fun newSensorValue(floatOrientation: FloatArray) {
        if (searchedDegree != 0F.toDouble()) {
            //Set arrow to North
            angle = floatOrientation[0] * 180 / Math.PI
            binding.arrow.rotation = angle.toFloat()
            Log.d(
                "Debug for Presentation",
                "Aktuell:" + angle + " Gesucht:" + searchedDegree + " Erste:" + firstDetection
            )
            // Check if the angle is in the searched range
            if (searchedDegree + 15 >= angle && searchedDegree - 15 <= angle) {
                if (firstDetection == 0) {
                    firstDetection = timer
                    //Check if the angle is correct for 2 to 5 seconds
                } else if (timer - firstDetection in 2..5) {
                    //vibrate
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(500)
                    }
                    //Stop timer
                    viewModel.timerService?.stopTimer(this)
                    viewModel.stopSensor()
                    //Get next Object
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
                //Out of range
            } else {
                firstDetection = 0
                background.setBackgroundColor(Color.parseColor("#393E46"))
            }
        }
    }
    /** Timer listener **/
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