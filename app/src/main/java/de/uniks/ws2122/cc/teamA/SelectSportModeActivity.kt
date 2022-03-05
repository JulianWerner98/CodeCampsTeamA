package de.uniks.ws2122.cc.teamA

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import de.uniks.ws2122.cc.teamA.databinding.ActivitySelectSportModeBinding
import de.uniks.ws2122.cc.teamA.model.util.SportMode
import de.uniks.ws2122.cc.teamA.repository.SportChallengeRepository

class SelectSportModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectSportModeBinding
    private val sportRepo = SportChallengeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySelectSportModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.isClickable = false

        sportRepo.hasRunningGame { isRunning ->

            if (isRunning) {

                val intent = Intent(this, SportChallengesActivity::class.java)
                startActivity(intent)

            } else {

                binding.radioGroupMode.setOnCheckedChangeListener { radioGroup, _ ->

                    var mode = ""
                    when (radioGroup.checkedRadioButtonId) {

                        binding.rbTime.id -> mode = Constant.TIME
                        binding.rbMeters.id -> mode = Constant.METERS
                        binding.rbSteps.id -> mode = Constant.STEPS
                    }

                    showOption(SportMode().getOptions(mode))
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

    private fun showOption(options: ArrayList<String>) {

        for (i in 0 until binding.radioGroupOptions.size) {

            val radioButton = binding.radioGroupOptions.getChildAt(i)

            if (radioButton is RadioButton) {

                radioButton.text = options[i]
            }
        }
    }
}
