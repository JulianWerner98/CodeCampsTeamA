package de.uniks.ws2122.cc.teamA.mentalArithmetic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.GameSelectActivity
import de.uniks.ws2122.cc.teamA.databinding.ActivityResultBinding
import de.uniks.ws2122.cc.teamA.model.MentalArithmeticResultViewModel

class MentalArithmeticResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivityResultBinding
    private lateinit var viewModel: MentalArithmeticResultViewModel
    private lateinit var time : TextView
    private lateinit var correctAnswers : TextView
    private lateinit var wrongAnswers : TextView
    private lateinit var backToGameSelectBtn : Button
    private lateinit var wonGame : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        time = binding.tvTime
        correctAnswers = binding.tvCorrectAnswers
        wrongAnswers = binding.tvWrongAnswers
        backToGameSelectBtn = binding.btnBackToGameSelect
        wonGame = binding.tvWonGame

        viewModel = ViewModelProvider(this)[MentalArithmeticResultViewModel::class.java]

        initializeObserver()

        backToGameSelectBtn.setOnClickListener {
            val intent = Intent(this, GameSelectActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeObserver() {
        viewModel.getLiveCurrentUserCorrectAnswersData().observe(this, Observer {
            val points = it.toInt() * 100
            val text = ("$it correct answers and $points points")
            correctAnswers.text = text
        })

        viewModel.getLiveCurrentUserWrongAnswersData().observe(this, Observer {
            val text = "$it wrong answers"
            wrongAnswers.text = text
        })

        viewModel.getLiveTimeData().observe(this, Observer {
            time.text = it
        })

        viewModel.getLiveWonGameData().observe(this, Observer {
            wonGame.text = it
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.finishedGame()
    }

}