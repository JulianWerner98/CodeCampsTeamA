package de.uniks.ws2122.cc.teamA.model.sportChallenge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.model.util.SportMode
import de.uniks.ws2122.cc.teamA.model.util.StepCounter
import de.uniks.ws2122.cc.teamA.model.util.StepTimerService
import de.uniks.ws2122.cc.teamA.repository.SportChallengeRepository
import kotlin.math.roundToInt

class SportChallengeViewModel : ViewModel() {

    private val sportChallengeData: MutableLiveData<SportChallenge> = MutableLiveData()
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private lateinit var sportRepo: SportChallengeRepository
    private lateinit var stepCounter: StepCounter
    private lateinit var appContext: Context

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

        sportRepo = SportChallengeRepository()

        sportChallengeData.value!!.mode = mode
        sportChallengeData.value!!.option = option
        setSportChallengeData(sportChallengeData.value!!)

        if (mode.isEmpty()) {

            sportRepo.getModeAndOption { modeRepo, optionRepo ->

                sportChallengeData.value!!.mode = modeRepo
                sportChallengeData.value!!.option = optionRepo
                setSportChallengeData(sportChallengeData.value!!)
            }
        }

        sportRepo.startMatchMaking(mode, option) { enemy, user ->

            stepCounter = StepCounter(context)

            sportChallengeData.value!!.players = mutableListOf(user, enemy)
            setSportChallengeData(sportChallengeData.value!!)

            appContext = context.applicationContext
            createTimer(appContext)
            startTimer(appContext)

            stepCounter.startSteps { steps ->

                sportChallengeData.value!!.userCountedSteps = steps
                sportChallengeData.value!!.userMeters = (steps * 0.7f * 10.0f).roundToInt() / 10.0f

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
            var speed = .0f
            val unit =
                if (sportChallengeData.value!!.mode == Constant.METERS) sportChallengeData.value!!.userMeters else sportChallengeData.value!!.userCountedSteps.toFloat()

            if (time > 0) {

                speed = ((unit / time.toFloat()) * 3.6f * 10.0f).roundToInt() / 10.0f

            }
            sportChallengeData.value!!.userSpeed = speed
            sportChallengeData.value!!.userTime = time
            setSportChallengeData(sportChallengeData.value!!)

            sportRepo.sendData(
                sportChallengeData.value!!.players[0],
                sportChallengeData.value!!.userCountedSteps,
                sportChallengeData.value!!.userMeters
            )

            checkFinished(context)
        }
    }

    private fun createTimer(context: Context) {

        serviceIntent = Intent(context, StepTimerService::class.java)
        context.registerReceiver(
            updateTime,
            IntentFilter(StepTimerService.TIMER_UPDATE)
        )
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
                time = ((currentTime - oldSystemTime) + countedTime)
            }
            callback.invoke(true)
        }
    }

    private fun stopTimer(context: Context) {

        context.stopService(serviceIntent)

    }

    private fun isWinner() {

        var value = sportChallengeData.value!!

        if (value.mode == Constant.TIME) {

            if (value.userCountedSteps > value.enemyCountedSteps) {

                value.winner = value.players[0]
            }

            if (value.userCountedSteps < value.enemyCountedSteps) {

                value.winner = value.players[1]
            }

            if (value.userCountedSteps == value.enemyCountedSteps) {

                value.winner = "Tie"
            }
        }
        else {

            if (value.userTime > value.enemyTime) {

                value.winner = value.players[0]
            }

            if (value.userTime < value.enemyTime) {

                value.winner = value.players[1]
            }

            if (value.userTime == value.enemyTime) {

                value.winner = "Tie"
            }
        }

        setSportChallengeData(value)
    }

    private fun checkFinished(context: Context) {

        val value = sportChallengeData.value!!

        val mode = value.mode
        val option = value.option
        val meters = value.userMeters
        val steps = value.userCountedSteps
        val goal = SportMode().getOptionValue(mode, option)

        val isFinished = when (mode) {

            Constant.TIME -> goal <= time
            Constant.METERS -> goal <= meters
            Constant.STEPS -> goal <= steps
            else -> false
        }

        if (isFinished) {

            stopTimer(context)
            stepCounter.resetSteps()
            sportRepo.sendResult(value.players[0], time, steps, meters)

            sportRepo.getEnemyResults(value.players[1]) { time, steps, meters ->

                value.enemyTime = time
                value.enemyCountedSteps = steps
                value.enemyMeters = meters
                setSportChallengeData(value)

                isWinner()
            }
        }
    }

    fun cancelMatch() {

        sportRepo.cancelMatch()
    }

    fun surrenderMatch() {

        sportRepo.surrenderMatch(sportChallengeData.value!!.players[0])
        stopTimer(appContext)
        stepCounter.resetSteps()
    }

    fun deleteMatch(){

        sportRepo.deleteMatch(sportChallengeData.value!!.players[1])
    }
}
