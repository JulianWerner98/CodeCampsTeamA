package de.uniks.ws2122.cc.teamA

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivitySportChallengesBinding
import de.uniks.ws2122.cc.teamA.model.sportChallenge.SportChallenge
import de.uniks.ws2122.cc.teamA.model.sportChallenge.SportChallengeViewModel

class SportChallengesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySportChallengesBinding
    private lateinit var viewModel: SportChallengeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySportChallengesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SportChallengeViewModel::class.java]

        val extras = intent.extras
        var mode = ""
        var option = ""
        if (extras != null) {

            mode = extras.get(Constant.MODE).toString()
            option = extras.get(Constant.OPTION).toString()
        }

        viewModel.startMatch(mode, option, this)
        createDataObserver()
    }

    private fun createDataObserver() {

        viewModel.getSportChallengeData().observe(this) { sportChallenge ->

            binding.tvTimeCounter.text = "${sportChallenge.userTime}"

            if (sportChallenge!!.mode == Constant.METERS) {

                showMetersText(sportChallenge)

            } else {
                showStepsText(sportChallenge)
            }
        }
    }

    private fun showStepsText(sportChallenge: SportChallenge) {

        binding.tvUserCounterText.text = "My Steps"
        binding.tvEnemyCounterText.text = "Enemy Steps"
        binding.tvStats1.text = "Meters"
        binding.tvStats2.text = "steps/h"

        binding.tvCounterUser.text = "${sportChallenge.userCountedSteps}"
        binding.tvCounterEnemy.text = "${sportChallenge.enemyCountedSteps}"
        binding.tvCounterStats1.text = "${sportChallenge.userMeters}"
        binding.tvCounterStats2.text = "${sportChallenge.userSpeed}"

    }

    private fun showMetersText(sportChallenge: SportChallenge) {

        binding.tvUserCounterText.text = "My Meters"
        binding.tvEnemyCounterText.text = "Enemy Meters"
        binding.tvStats1.text = "Steps"
        binding.tvStats2.text = "km/h"

        binding.tvCounterUser.text = "${sportChallenge.userMeters}"
        binding.tvCounterEnemy.text = "${sportChallenge.enemyMeters}"
        binding.tvCounterStats1.text = "${sportChallenge.userCountedSteps}"
        binding.tvCounterStats2.text = "${sportChallenge.userSpeed}"
    }

    override fun onDestroy() {
        viewModel.saveTime()
        super.onDestroy()
    }
}