package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.repository.MentalArithmeticRepository

class MentalArithmeticResultViewModel : ViewModel() {
    private var currentUserAnswers = mutableListOf<Boolean>()
    private var opponentAnswers = mutableListOf<Boolean>()
    private var gameKey = String()
    private var currentUserCorrectAnswers = 0
    private var currentUserWrongAnswers = 0
    private var opponentCorrectAnswers = 0
    private var opponentWrongAnswers = 0
    private var time = String()
    private var opponentTime = String()
    private var wonGame = String()


    // Live Data
    private var currentUserAnswersData = MutableLiveData<List<Boolean>>()
    private var currentUserCorrectAnswersData = MutableLiveData<String>()
    private var currentUserWrongAnswersData = MutableLiveData<String>()
    private var wonGameData = MutableLiveData<String>()
    private var timeData = MutableLiveData<String>()

    // Repo
    private var mentalArithmeticRepo = MentalArithmeticRepository()

    init {
        currentUserAnswersData.value = currentUserAnswers
        mentalArithmeticRepo.fetchGameKey { key ->
            gameKey = key
            fetchAnswers(gameKey)
            fetchTime(gameKey)
        }
    }


    // Setter
    fun setLiveCurrentUserAnswersData(){
        currentUserAnswersData.value = currentUserAnswers
        println(currentUserAnswersData.value)
    }

    fun setLiveWonGameData(){
        wonGameData.value = wonGame
    }

    fun setLiveCurrentUserWrongAnswersData(){
        currentUserWrongAnswersData.value = currentUserWrongAnswers.toString()
    }

    fun setLiveCurrentUserCorrectAnswersData(){
        currentUserCorrectAnswersData.value = currentUserCorrectAnswers.toString()
    }

    fun setLiveTimeData(){
        timeData.value = time
    }

    // Getter
    fun getLiveCurrentUserAnswersData(): MutableLiveData<List<Boolean>> {
        return currentUserAnswersData
    }

    fun getLiveWonGameData(): MutableLiveData<String> {
        return wonGameData
    }

    fun getLiveCurrentUserWrongAnswersData(): MutableLiveData<String> {
        return currentUserWrongAnswersData
    }

    fun getLiveCurrentUserCorrectAnswersData(): MutableLiveData<String> {
        return currentUserCorrectAnswersData
    }

    fun getLiveTimeData(): MutableLiveData<String>{
        return timeData
    }

    // Logic
    // Fetch your answers
    private fun fetchAnswers(gameKey: String) {
        mentalArithmeticRepo.fetchCurrentUserAnswers(gameKey) {
            currentUserAnswers = it
            setLiveCurrentUserAnswersData()
            getCorrectAndWrongAnswers(currentUserAnswers)
            mentalArithmeticRepo.fetchOpponentAnswers(gameKey) { answer ->
                opponentAnswers = answer
                getWonGame(opponentAnswers)
            }
        }
    }

    // Fetch your time
    private fun fetchTime(gameKey: String) {
        mentalArithmeticRepo.fetchTime(gameKey) {
            time = it[0]
            opponentTime = it[1]
        }
    }

    // Get your correct and wrong answers number
    fun getCorrectAndWrongAnswers(currentUserAnswers: MutableList<Boolean>){
        for (answer in currentUserAnswers) {
            if (answer) {
                currentUserCorrectAnswers += 1
            } else {
                currentUserWrongAnswers += 1
            }
        }
        setLiveCurrentUserCorrectAnswersData()
        setLiveCurrentUserWrongAnswersData()
    }

    // Check the winner
    fun getWonGame(opponentAnswers: MutableList<Boolean>){
        for (answer in opponentAnswers){
            if (answer) {
                opponentCorrectAnswers += 1
            } else {
                opponentWrongAnswers += 1
            }
        }

        // Calculate your and opponents time and set winner of that game
        when(calculateTime()){
            Constant.MORETIME -> {
                if(currentUserCorrectAnswers > opponentCorrectAnswers) {
                    wonGame = "You have won"
                    mentalArithmeticRepo.setMentalArithmeticWin(currentUserCorrectAnswers, gameKey)
                    setLiveWonGameData()
                } else {
                    wonGame = "You have lost"
                    mentalArithmeticRepo.setMentalArithmeticLose(gameKey)
                    setLiveWonGameData()
                }
            }
            Constant.LESSTIME -> {
                if (currentUserCorrectAnswers >= opponentCorrectAnswers){
                    wonGame = "You have won"
                    mentalArithmeticRepo.setMentalArithmeticWin(currentUserCorrectAnswers, gameKey)
                    setLiveWonGameData()
                } else {
                    wonGame = "You have lost"
                    mentalArithmeticRepo.setMentalArithmeticLose(gameKey)
                    setLiveWonGameData()
                }
            }
            Constant.SAMETIME -> {
                if (currentUserCorrectAnswers > opponentCorrectAnswers){
                    wonGame = "You have won"
                    mentalArithmeticRepo.setMentalArithmeticWin(currentUserCorrectAnswers, gameKey)
                    setLiveWonGameData()
                } else {
                    if (currentUserCorrectAnswers < opponentCorrectAnswers){
                        wonGame = "You have lost"
                        mentalArithmeticRepo.setMentalArithmeticLose(gameKey)
                        setLiveWonGameData()
                    } else {
                        wonGame = "It's a draw"
                        mentalArithmeticRepo.setMentalArithmeticDraw(gameKey)
                        setLiveWonGameData()
                    }
                }
            }
        }
    }

    private fun calculateTime(): String {
        var currentUserMinutes = time.split(":")[0].toInt()
        var currentUserSec = time.split(":")[1].toInt()
        val currentUserTimePenalty = 5 * currentUserWrongAnswers
        var opponentMinutes = opponentTime.split(":")[0].toInt()
        var opponentSec = opponentTime.split(":")[1].toInt()
        val opponentTimePenalty = 5 * opponentWrongAnswers

        currentUserSec += currentUserTimePenalty
        opponentSec += opponentTimePenalty

        // Add 5sec penalty for every wrong answer to your time
        if (currentUserSec >= 60){
            val minute = currentUserSec / 60
            currentUserMinutes += minute
            currentUserSec -= 60 * minute
        }

        // Add 5sec penalty for every wrong answer to your opponents
        if (opponentSec >= 60){
            val minute = opponentSec / 60
            opponentMinutes += minute
            opponentSec -= 60 * minute
        }

        time = "$currentUserMinutes:$currentUserSec"
        setLiveTimeData()
        opponentTime = "$opponentMinutes:$opponentSec"

        if (currentUserMinutes > opponentMinutes){
            return Constant.MORETIME
        }
        if (currentUserSec > opponentSec) {
            return Constant.MORETIME
        }
        return if ((currentUserMinutes == opponentMinutes) && (currentUserSec == opponentSec)){
            Constant.SAMETIME
        } else {
            Constant.LESSTIME
        }
    }

    // Finished the game
    fun finishedGame() {
        mentalArithmeticRepo.finishedGame(gameKey)
    }

}