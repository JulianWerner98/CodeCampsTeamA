package de.uniks.ws2122.cc.teamA.model.SportChallenges

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.model.util.StepCounter
import de.uniks.ws2122.cc.teamA.model.util.StepTimerService
import de.uniks.ws2122.cc.teamA.repository.SportChallengeRepository
import kotlin.math.roundToInt

class SportChallengeViewModel : ViewModel() {

    private val sportChallengeData: MutableLiveData<SportChallenge> = MutableLiveData()
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    init {
        setSportChallengeData(SportChallenge())
    }

    fun getSportChallengeData(): LiveData<SportChallenge> {

        return sportChallengeData
    }

    private fun setSportChallengeData(value: SportChallenge) {

        sportChallengeData.value = value
    }

    fun startMatch(mode: String, option: String, context: Context) {

        val sportRepo = SportChallengeRepository()
        sportRepo.startMatchMaking(mode, option) { enemy ->

            val stepCounter = StepCounter(context)

            createTimer(context.applicationContext)
            startTimer(context.applicationContext)

            stepCounter.startSteps {

                sportChallengeData.value!!.userCountedSteps = it
                setSportChallengeData(sportChallengeData.value!!)
            }

            sportRepo.createStepListener(enemy) { enemyStats ->

                sportChallengeData.value!!.enemyCountedSteps = enemyStats.first.toInt()
                sportChallengeData.value!!.enemyMeters = enemyStats.second.toFloat()
                setSportChallengeData(sportChallengeData.value!!)
            }
        }
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            time = intent.getDoubleExtra(StepTimerService.TIME_EXTRA, 0.0)
            sportChallengeData.value!!.userTime = getTimeStringFromDouble(time)
            setSportChallengeData(sportChallengeData.value!!)
        }
    }

    private fun createTimer(context: Context) {

        serviceIntent = Intent(context, StepTimerService::class.java)
        context.registerReceiver(
            updateTime,
            IntentFilter(StepTimerService.TIMER_UPDATE)
        )
    }

    private fun getTimeStringFromDouble(time: Double): String {

        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60

        return "$hours : $minutes : $seconds"
    }

    private fun startTimer(context: Context) {

        serviceIntent.putExtra(StepTimerService.TIME_EXTRA, time)
        context.startService(serviceIntent)
    }

    private fun stopTimer(context: Context) {

        context.stopService(serviceIntent)
    }
}
