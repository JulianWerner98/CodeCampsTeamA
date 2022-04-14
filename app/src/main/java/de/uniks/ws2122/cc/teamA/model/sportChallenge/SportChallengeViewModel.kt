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
import de.uniks.ws2122.cc.teamA.model.MatchResult
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

    /** get sport challenge live data**/
    fun getSportChallengeData(): LiveData<SportChallenge> {

        return sportChallengeData
    }

    /** set sport challenge live data **/
    private fun setSportChallengeData(value: SportChallenge) {

        sportChallengeData.value = value
    }

    /** starts the game and everything that is needed **/
    fun startMatch(mode: String, option: String, context: Context) {

        sportRepo = SportChallengeRepository()

        sportChallengeData.value!!.mode = mode
        sportChallengeData.value!!.option = option
        setSportChallengeData(sportChallengeData.value!!)

        //if mode is empty then a game already exists
        if (mode.isEmpty()) {

            //get mode and option from database
            sportRepo.getModeAndOption { modeRepo, optionRepo ->

                sportChallengeData.value!!.mode = modeRepo
                sportChallengeData.value!!.option = optionRepo
                setSportChallengeData(sportChallengeData.value!!)
            }
        }

        //start match making
        sportRepo.startMatchMaking(mode, option) { enemy, user ->

            stepCounter = StepCounter(context)

            sportChallengeData.value!!.players = mutableListOf(user, enemy)
            setSportChallengeData(sportChallengeData.value!!)

            //start timer
            appContext = context.applicationContext
            createTimer(appContext)
            startTimer(appContext)

            //start step counter
            stepCounter.startSteps { steps ->

                sportChallengeData.value!!.userCountedSteps = steps

                //0.7f meter = average step length of german citizen
                //10.0f = round to two decimal places
                sportChallengeData.value!!.userMeters = (steps * 0.7f * 10.0f).roundToInt() / 10.0f

                setSportChallengeData(sportChallengeData.value!!)
            }

            //place listener on the opponent in the database
            sportRepo.createStepListener(enemy) { steps, meters ->

                sportChallengeData.value!!.enemyCountedSteps = steps
                sportChallengeData.value!!.enemyMeters = meters
                setSportChallengeData(sportChallengeData.value!!)
            }
        }
    }

    /** create broadcast receiver to get current time **/
    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {

        //triggers each second
        override fun onReceive(context: Context, intent: Intent) {

            time = intent.getDoubleExtra(StepTimerService.TIME_EXTRA, 0.0)
            var speed = .0f
            val unit =
                if (sportChallengeData.value!!.mode == Constant.METERS) sportChallengeData.value!!.userMeters else sportChallengeData.value!!.userCountedSteps.toFloat()

            // m/s * 3.6 = km/h
            if (time > 0) {

                speed = ((unit / time.toFloat()) * 3.6f * 10.0f).roundToInt() / 10.0f

            }
            //set user speed and time
            sportChallengeData.value!!.userSpeed = speed
            sportChallengeData.value!!.userTime = time
            setSportChallengeData(sportChallengeData.value!!)

            //send user current steps/ meters to the database
            sportRepo.sendData(
                sportChallengeData.value!!.players[0],
                sportChallengeData.value!!.userCountedSteps,
                sportChallengeData.value!!.userMeters
            )

            checkFinished(context)
        }
    }

    /** create Timer **/
    private fun createTimer(context: Context) {

        serviceIntent = Intent(context, StepTimerService::class.java)
        context.registerReceiver(
            updateTime,
            IntentFilter(StepTimerService.TIMER_UPDATE)
        )
    }

    /** start timer **/
    private fun startTimer(context: Context) {

        loadTime {
            if (it) {

                serviceIntent.putExtra(StepTimerService.TIME_EXTRA, time)
                context.startService(serviceIntent)
            }
        }
    }

    /** save time in the database **/
    fun saveTime() {

        sportRepo.saveTime(time, sportChallengeData.value!!.players[0])
    }

    /** load time from the database **/
    private fun loadTime(callback: (loaded: Boolean) -> Unit) {

        sportRepo.loadTime(sportChallengeData.value!!.players[0]) { countedTime, oldSystemTimeInMilli ->

            //current time = current system time - saved system time
            if (countedTime > 0) {

                val currentTime = System.currentTimeMillis() / 1000
                val oldSystemTime = oldSystemTimeInMilli / 1000
                time = ((currentTime - oldSystemTime) + countedTime)
            }
            callback.invoke(true)
        }
    }

    /** stop timer **/
    private fun stopTimer(context: Context) {

        context.stopService(serviceIntent)

    }

    /** looks for a win and set the winner **/
    private fun isWinner() {

        val value = sportChallengeData.value!!

        if (value.mode == Constant.TIME) {

            if (value.userCountedSteps > value.enemyCountedSteps) {

                value.winner = value.players[0]
            }

            if (value.userCountedSteps < value.enemyCountedSteps) {

                value.winner = value.players[1]
            }

            if (value.userCountedSteps == value.enemyCountedSteps) {

                value.winner = Constant.DRAW
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

                value.winner = Constant.DRAW
            }
        }

        setSportChallengeData(value)
    }

    /** Checks if the challenge has been completed **/
    private fun checkFinished(context: Context) {

        val value = sportChallengeData.value!!

        val mode = value.mode
        val option = value.option
        val meters = value.userMeters
        val steps = value.userCountedSteps
        val goal = SportMode().getOptionValue(mode, option)

        //goal was reached?
        val isFinished = when (mode) {

            Constant.TIME -> goal <= time
            Constant.METERS -> goal <= meters
            Constant.STEPS -> goal <= steps
            else -> false
        }

        //stop timer and counter
        //send final results to the database
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
                saveMatchResult()
            }
        }
    }

    /** save match result for match history **/
    private fun saveMatchResult() {

        val value = sportChallengeData.value!!
        val matchResult = MatchResult()

        matchResult.currentuser = "You"
        matchResult.gamename = Constant.SPORT_CHALLENGE
        matchResult.points = value.userCountedSteps * value.userTime.toInt()

        when (value.winner) {

            value.players[0] -> matchResult.win = Constant.WIN
            value.players[1] -> matchResult.win = Constant.LOSE
            else -> matchResult.win = Constant.DRAW
        }

        sportRepo.getEnemyName(value.players[1]) {
            
            matchResult.opponent = it
            sportRepo.saveMatchResult(matchResult)
        }
    }

    /** cancel match **/
    fun cancelMatch() {

        sportRepo.cancelMatch()
    }

    /** surrender match **/
    fun surrenderMatch() {

        //save match results
        //stop timer and step counter
        saveMatchResult()
        sportRepo.surrenderMatch(sportChallengeData.value!!.players[0])
        stopTimer(appContext)
        stepCounter.resetSteps()
    }

    /** delete match after the game **/
    fun deleteMatch(){

        sportRepo.deleteMatch(sportChallengeData.value!!.players[1])
    }
}
