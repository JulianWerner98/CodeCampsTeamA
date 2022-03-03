package de.uniks.ws2122.cc.teamA.model.SportChallenges

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
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
    private lateinit var sportRepo: SportChallengeRepository

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


        sportChallengeData.value!!.mode = mode
        sportChallengeData.value!!.option = option
        setSportChallengeData(sportChallengeData.value!!)

        sportRepo = SportChallengeRepository()
        sportRepo.startMatchMaking(mode, option) { enemy, user ->

            val stepCounter = StepCounter(context)

            sportChallengeData.value!!.players = mutableListOf(user, enemy)

            createTimer(context.applicationContext)
            startTimer(context.applicationContext)

            stepCounter.startSteps {

                sportChallengeData.value!!.userCountedSteps = it
                setSportChallengeData(sportChallengeData.value!!)
            }

            sportRepo.createStepListener(enemy) { steps, meters ->

                sportChallengeData.value!!.enemyCountedSteps = steps
                sportChallengeData.value!!.enemyMeters = meters
                setSportChallengeData(sportChallengeData.value!!)
            }
        }
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            time = intent.getDoubleExtra(StepTimerService.TIME_EXTRA, 0.0)
            sportChallengeData.value!!.userTime = getTimeStringFromDouble(time)
            setSportChallengeData(sportChallengeData.value!!)
            sportRepo.sendData(
                sportChallengeData.value!!.players[0],
                sportChallengeData.value!!.userCountedSteps,
                sportChallengeData.value!!.userMeters
            )
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

        loadTime {
            if (it) {

                serviceIntent.putExtra(StepTimerService.TIME_EXTRA, time)
                context.startService(serviceIntent)
            }
        }
    }

    fun saveTime() {

        sportRepo.saveTime(time, sportChallengeData.value!!.players[0])
    }

    private fun loadTime(callback: (loaded: Boolean) -> Unit) {

        sportRepo.loadTime(sportChallengeData.value!!.players[0]) { countedTime, oldSystemTimeInMilli ->

            if (countedTime > 0) {

                val currentTime = System.currentTimeMillis() / 1000
                val oldSystemTime = oldSystemTimeInMilli / 1000
                time = ((currentTime - oldSystemTime)  + countedTime)
            }
            callback.invoke(true)
        }
    }

    private fun stopTimer(context: Context) {

        context.stopService(serviceIntent)
    }
}
