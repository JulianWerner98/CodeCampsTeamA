package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import de.uniks.ws2122.cc.teamA.databinding.ActivitySelectSportModeBinding
import de.uniks.ws2122.cc.teamA.repository.SportChallengeRepository

class SelectSportModeActivity : AppCompatActivity() {

    private val timeOptions =
        arrayOf("1 Minute", "5 Minutes", "10 Minutes", "30 Minutes", "60 Minutes")
    private val meterOptions =
        arrayOf("100 Meters", "500 Meters", "1 Kilometer", "2 Kilometers", "5 Kilometers")
    private val stepsOption =
        arrayOf("100 Steps", "200 Steps", "500 Steps", "1000 Steps", "10.000 Steps")

    private lateinit var binding: ActivitySelectSportModeBinding
    private val sportRepo = SportChallengeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySelectSportModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.isClickable = false

        sportRepo.hasRunningGame {

            if (it) {

                Log.d("TAG", "Hallo")
                val intent = Intent(this, SportChallengesActivity::class.java)
                startActivity(intent)

            } else {

                binding.radioGroupMode.setOnCheckedChangeListener { radioGroup, _ ->

                    when (radioGroup.checkedRadioButtonId) {

                        binding.rbTime.id -> showOption(timeOptions)
                        binding.rbMeters.id -> showOption(meterOptions)
                        binding.rbSteps.id -> showOption(stepsOption)
                    }
                }

                binding.btnStart.isClickable = true
                binding.btnStart.setOnClickListener {

                    val selectedModeRadioButton =
                        findViewById<RadioButton>(binding.radioGroupMode.checkedRadioButtonId)
                    val selectedOptionRadioButton =
                        findViewById<RadioButton>(binding.radioGroupOptions.checkedRadioButtonId)
                    val mode = selectedModeRadioButton.text.toString()
                    val option = selectedOptionRadioButton.text.toString()

                    val intent = Intent(this, SportChallengesActivity::class.java).apply {
                        this.putExtra(Constant.MODE, mode)
                        this.putExtra(Constant.OPTION, option)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun showOption(options: Array<String>) {

        for (i in 0 until binding.radioGroupOptions.size) {

            val radioButton = binding.radioGroupOptions.getChildAt(i)

            if (radioButton is RadioButton) {

                radioButton.text = options[i]
            }
        }
    }
}
