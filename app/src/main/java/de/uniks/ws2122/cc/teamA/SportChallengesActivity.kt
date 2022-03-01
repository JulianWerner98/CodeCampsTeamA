package de.uniks.ws2122.cc.teamA

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        viewModel.setSportChallengeData(SportChallenge()) //Testing
        createDataObserver()
        viewModel.countSteps(this)
    }

    // deutsche Durchschnittsgröße ist ~173cm. durchschnittliche Schrittlänge bei 170cm ist ~70cm
    private fun createDataObserver() {

        val data = viewModel.getSportChallengeData().value

        if (data!!.mode == "meters") {

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

            if (sportChallenge.mode == "meters") {

                binding.tvCounterUser.text = "${0.7f * sportChallenge.userCountedSteps}"
                binding.tvCounterEnemy.text = "${0.7f * sportChallenge.enemyCountedSteps}"
                binding.tvCounterStats1.text = "${sportChallenge.userCountedSteps}"
            } else {

                binding.tvCounterUser.text = "${sportChallenge.userCountedSteps}"
                binding.tvCounterEnemy.text = "${sportChallenge.enemyCountedSteps}"
                binding.tvCounterStats1.text = "${0.7f * sportChallenge.userCountedSteps}"
            }

            binding.tvStats2.text = "Steps per Minute"
            binding.tvCounterStats2.text =
                "${sportChallenge.userCountedSteps / sportChallenge.userTime}"
        }
    }
}