package de.uniks.ws2122.cc.teamA.mentalArithmetic

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

        viewModel = ViewModelProvider(this)[MentalArithmeticViewModel::class.java]

        startButton.setOnClickListener {
            //startActivity(Intent(this, MentalArithmeticResultActivity::class.java))
            viewModel.readyUpToStartGame()
        }

        sendAnswerBtn.setOnClickListener {
            if (answer.text.isNotEmpty()){
                viewModel.sendTaskAnswer(answer.text.toString())
                answer.text.clear()
            }
        }

        viewModel.getLiveCurrentUserAnswersData().observe(this, Observer {
            if (running){
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
            }
            arithmeticTask.text = viewModel.getCurrentTask()
            running = true
        })
    }

    fun pauseChronometer(v : View){
        if (running){
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            running = false
            System.out.println(chronometer.text)
        }
    }
}