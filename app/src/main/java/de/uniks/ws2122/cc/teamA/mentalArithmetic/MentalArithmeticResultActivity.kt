package de.uniks.ws2122.cc.teamA.mentalArithmetic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivityResultBinding
import de.uniks.ws2122.cc.teamA.model.MentalArithmeticViewModel

class MentalArithmeticResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivityResultBinding
    private lateinit var viewModel: MentalArithmeticViewModel
    private lateinit var highscore : TextView
    private lateinit var time : TextView
    private lateinit var correctAnswers : TextView
    private lateinit var wrongAnswers : TextView
    private lateinit var backToGameSelectBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        highscore = binding.tvNewHighscore
        time = binding.tvTime
        correctAnswers = binding.tvCorrectAnswers
        wrongAnswers = binding.tvWrongAnswers
        backToGameSelectBtn = binding.btnBackToGameSelect

        viewModel = ViewModelProvider(this)[MentalArithmeticViewModel::class.java]
    }
}