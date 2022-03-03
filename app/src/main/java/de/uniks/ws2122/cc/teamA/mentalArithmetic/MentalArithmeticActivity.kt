package de.uniks.ws2122.cc.teamA.mentalArithmetic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.GameSelectActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityMentalArithmeticBinding
import de.uniks.ws2122.cc.teamA.model.MentalArithmeticViewModel

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

        viewModel = ViewModelProvider(this)[MentalArithmeticViewModel::class.java].apply {
            this.setMatchTyp(friendId, matchTyp, inviteKey)
        }

        viewModel.makeGame()

        startButton.setOnClickListener {
            //startActivity(Intent(this, MentalArithmeticResultActivity::class.java))
            if (startButton.text == Constant.START){
                viewModel.readyUpToStartGame()
                startButton.text = Constant.SURRENDER
            } else {
                val intent = Intent(this, GameSelectActivity::class.java).apply {  }
                startActivity(intent)
                finish()
            }
        }

        sendAnswerBtn.setOnClickListener {
            if (answer.text.isNotEmpty()){
                viewModel.sendTaskAnswer(answer.text.toString())
                answer.text.clear()
            }
        }
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
        viewModel.chronometer(chronometer, pauseOffset)

        viewModel.getLiveCurrentUserAnswersData().observe(this, Observer {
            val currentTask = viewModel.getCurrentTask(){ result ->
                if (result){
                    sendAnswerBtn.isEnabled = true
                }
            }
            if (currentTask == Constant.WAITINGFOROPPONENT){
                arithmeticTask.text = currentTask
                sendAnswerBtn.isEnabled = false
                chronometer.stop()
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