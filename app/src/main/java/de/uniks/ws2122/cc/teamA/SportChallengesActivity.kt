package de.uniks.ws2122.cc.teamA

import android.content.BroadcastReceiver
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivitySportChallengesBinding
import de.uniks.ws2122.cc.teamA.model.SportChallenges.SportChallenge
import de.uniks.ws2122.cc.teamA.model.SportChallenges.SportChallengeViewModel

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

        val data = viewModel.getSportChallengeData().value
        Log.d("TAG", "Mode: ${data!!.mode}")

        if (data!!.mode == Constant.METERS) {

            binding.tvUserCounterText.text = "User Meters"
            binding.tvEnemyCounterText.text = "Enemy Meters"
            binding.tvStats1.text = "Steps"
        } else {

            binding.tvUserCounterText.text = "User Steps"
            binding.tvEnemyCounterText.text = "Enemy Steps"
            binding.tvStats1.text = "Meters"
        }

        viewModel.getSportChallengeData().observe(this) { sportChallenge ->

            binding.tvTimeCounter.text = "${sportChallenge.userTime}"

            if (sportChallenge.mode == Constant.METERS) {

                binding.tvCounterUser.text = "${sportChallenge.userMeters}"
                binding.tvCounterEnemy.text = "${sportChallenge.enemyMeters}"
                binding.tvCounterStats1.text = "${sportChallenge.userCountedSteps}"
            } else {

                binding.tvCounterUser.text = "${sportChallenge.userCountedSteps}"
                binding.tvCounterEnemy.text = "${sportChallenge.enemyCountedSteps}"
                binding.tvCounterStats1.text = "${sportChallenge.userMeters}"
            }

            binding.tvStats2.text = "Steps per Minute"
            binding.tvCounterStats2.text = "${sportChallenge.userSpeed}"
        }
    }

    override fun onDestroy() {
        viewModel.saveTime()
        super.onDestroy()
    }
}