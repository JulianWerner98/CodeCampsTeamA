package de.uniks.ws2122.cc.teamA.mentalArithmetic

import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.GameSelectActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityMentalArithmeticBinding
import de.uniks.ws2122.cc.teamA.friendlist.FriendRequestActivity
import de.uniks.ws2122.cc.teamA.model.MentalArithmeticViewModel
import de.uniks.ws2122.cc.teamA.model.util.Notifications

class MentalArithmeticActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMentalArithmeticBinding
    private lateinit var chronometer: Chronometer
    private lateinit var startButton: Button
    private lateinit var arithmeticTask : TextView
    private lateinit var answer : EditText
    private lateinit var sendAnswerBtn : Button
    private lateinit var viewModel : MentalArithmeticViewModel
    private lateinit var friendId : String
    private lateinit var matchTyp : String
    private lateinit var inviteKey : String

    private var running : Boolean = false
    private var pauseOffset : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMentalArithmeticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chronometer = binding.chronometerMentalArithmetic
        startButton = binding.btnGameStart
        arithmeticTask = binding.tvArithmeticTasks
        answer = binding.editTextAnswers
        sendAnswerBtn = binding.btnSendAnswer
        sendAnswerBtn.isEnabled = false

        friendId = intent.extras?.getString(Constant.FRIENDID).toString()
        matchTyp = intent.extras?.getString(Constant.MATCHTYP).toString()
        inviteKey = intent.extras?.getString(Constant.INVITEKEY).toString()

        // Create ViewModel
        viewModel = ViewModelProvider(this)[MentalArithmeticViewModel::class.java].apply {
            this.setMatchTyp(friendId, matchTyp, inviteKey)
        }

        // Make a game
        viewModel.makeGame()

        // Start or surrender your game
        startButton.setOnClickListener {
            if (startButton.text == Constant.START){
                viewModel.readyUpToStartGame()
                startButton.text = Constant.SURRENDER
            } else {
                val intent = Intent(this, GameSelectActivity::class.java).apply {  }
                startActivity(intent)
                finish()
            }
        }

        // Send your answer
        sendAnswerBtn.setOnClickListener {
            if (answer.text.isNotEmpty()){
                viewModel.sendTaskAnswer(answer.text.toString())
                answer.text.clear()
            }
        }

        // Set chronometer
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
        viewModel.chronometer(chronometer, pauseOffset)

        // Add observer on user answer and change if there is a new answer
        viewModel.getLiveCurrentUserAnswersData().observe(this, Observer {
            val currentTask = viewModel.getCurrentTask(){ result->
                if (result){
                    sendAnswerBtn.isEnabled = true
                }
            }
            // Stop timer and wait for opponent
            if (currentTask == Constant.WAITINGFOROPPONENT){
                arithmeticTask.text = currentTask
                sendAnswerBtn.isEnabled = false
                chronometer.stop()
                // Change to match result
                viewModel.goToResultActivity(){ it ->
                    if (it) {
                        val intent = Intent(this, MentalArithmeticResultActivity::class.java).apply {  }
                        startActivity(intent)
                        finish()
                    }
                }
            }
            else {
                arithmeticTask.text = currentTask
            }
            running = true
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroyGame()
    }
}