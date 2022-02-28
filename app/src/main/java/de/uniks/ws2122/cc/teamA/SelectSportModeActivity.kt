package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import de.uniks.ws2122.cc.teamA.databinding.ActivitySelectSportModeBinding

class SelectSportModeActivity : AppCompatActivity() {

    private val timeOptions =
        arrayOf("1 Minute", "5 Minutes", "10 Minutes", "30 Minutes", "60 Minutes")
    private val meterOptions =
        arrayOf("100 Meters", "500 Meters", "1 Kilometer", "2 Kilometers", "5 Kilometers")
    private val stepsOption =
        arrayOf("100 Steps", "200 Steps", "500 Steps", "1000 Steps", "10.000 Steps")

    private lateinit var binding: ActivitySelectSportModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySelectSportModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radioGroupMode.setOnCheckedChangeListener { radioGroup, _ ->

            when(radioGroup.checkedRadioButtonId) {

                binding.rbTime.id -> showOption(timeOptions)
                binding.rbMeters.id -> showOption(meterOptions)
                binding.rbSteps.id -> showOption(stepsOption)
            }
        }

        binding.btnStart.setOnClickListener {

            val intent = Intent(this, SportChallengesActivity::class.java)
            startActivity(intent)
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
