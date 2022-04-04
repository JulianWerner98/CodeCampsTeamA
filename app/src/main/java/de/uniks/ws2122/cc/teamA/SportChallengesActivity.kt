package de.uniks.ws2122.cc.teamA

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.uniks.ws2122.cc.teamA.databinding.ActivitySportChallengesBinding
import de.uniks.ws2122.cc.teamA.model.sportChallenge.SportChallenge
import de.uniks.ws2122.cc.teamA.model.sportChallenge.SportChallengeViewModel
import kotlin.math.roundToInt

class SportChallengesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySportChallengesBinding
    private lateinit var viewModel: SportChallengeViewModel
    private var dontSaveTime = false

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

        binding.button.text = "Cancel"
        binding.button.setOnClickListener {

            viewModel.cancelMatch()
            finish()
            binding.tvInfo.text = "Canceled"
            dontSaveTime = true
        }
    }

    private fun createDataObserver() {

        viewModel.getSportChallengeData().observe(this) { sportChallenge ->

            binding.tvTimeCounter.text = getTimeStringFromDouble(sportChallenge)

            if (sportChallenge!!.mode == Constant.METERS) {

                showMetersText(sportChallenge)

            } else {
                showStepsText(sportChallenge)
            }

            if (sportChallenge.players.size == 2) {

                binding.tvInfo.text = "Run"
                binding.button.text = "Surrender"
                binding.button.setOnClickListener {

                    viewModel.surrenderMatch()
                    finish()
                }
            }

            showWinner(sportChallenge)
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

    private fun showWinner(sportChallenge: SportChallenge) {

        if (sportChallenge.winner.isNotEmpty()) {

            if (sportChallenge.players[0] == sportChallenge.winner) {

                binding.tvInfo.text = "You won"
            }
            else {

                binding.tvInfo.text = "You lose"
            }

            if (sportChallenge.winner == "Tie") {

                binding.tvInfo.text = "Tie"
            }

            binding.button.text = "Leave"
            binding.button.setOnClickListener {

                viewModel.deleteMatch()
                dontSaveTime = true
                finish()
            }
        }
    }

    private fun getTimeStringFromDouble(sportChallenge: SportChallenge): String {

        val time = sportChallenge.userTime

        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60

        return "$hours : $minutes : $seconds"
    }

    override fun onDestroy() {

        if (!dontSaveTime) {

            viewModel.saveTime()
        }
        super.onDestroy()
    }
}