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
            saveTime(context, time)
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

        loadTime(context)
        serviceIntent.putExtra(StepTimerService.TIME_EXTRA, time)
        context.startService(serviceIntent)
    }

    fun saveTime(context: Context, time: Double){

        context.getSharedPreferences(TIME_STEPS_SHARED_PREFS, Context.MODE_PRIVATE).edit().apply {

            putLong(OLD_TIME, System.currentTimeMillis() / 1000)
            putLong(SAVED_TIMER, time.toLong())
        }.apply()
    }

    private fun loadTime(context: Context) {

        val sharedPreferences = context.getSharedPreferences(TIME_STEPS_SHARED_PREFS, Context.MODE_PRIVATE)
        val savedTime = sharedPreferences.getLong(SAVED_TIMER, 0)
        val oldTime = sharedPreferences.getLong(OLD_TIME, 0)
        val currentTime = System.currentTimeMillis() / 1000

        time = ((currentTime - oldTime) + savedTime).toDouble()
    }

    private fun stopTimer(context: Context) {

        context.stopService(serviceIntent)
    }

    companion object {

        const val TIME_STEPS_SHARED_PREFS = "TimeAndStepsSharedPref"
        const val OLD_TIME = "OldTime"
        const val SAVED_TIMER = "SavedTimer"
    }
}
