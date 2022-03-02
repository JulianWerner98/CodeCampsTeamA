package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.repository.MentalArithmeticRepository

class MentalArithmeticResultViewModel : ViewModel() {
    private var currentUserAnswers = mutableListOf<Boolean>()
    private var opponentAnswers = mutableListOf<Boolean>()
    private var gameKey = String()
    private var currentUserCorrectAnswers = 0
    private var currentUserWrongAnswers = 0
    private var opponentCorrectAnswers = 0
    private var time = String()
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
    private fun fetchAnswers(gameKey: String) {
        mentalArithmeticRepo.fetchCurrentUserAnswers(gameKey) {
            currentUserAnswers = it
            setLiveCurrentUserAnswersData()
            getCorrectAnswers(currentUserAnswers)
            getWrongAnswers(currentUserAnswers)
            mentalArithmeticRepo.fetchOpponentAnswers(gameKey) { answer ->
                opponentAnswers = answer
                getWonGame(opponentAnswers)
            }
        }
    }

    private fun fetchTime(gameKey: String) {
        mentalArithmeticRepo.fetchTime(gameKey) {
            time = it
            setLiveTimeData()
        }
    }


    fun getCorrectAnswers(currentUserAnswers: MutableList<Boolean>){
        println(currentUserAnswers)
        for (answer in currentUserAnswers) {
            if (answer) {
                currentUserCorrectAnswers += 1
            }
        }
        setLiveCurrentUserCorrectAnswersData()
    }

    fun getWrongAnswers(currentUserAnswers: MutableList<Boolean>){
        for (answer in currentUserAnswers){
            if (!answer) {
                currentUserWrongAnswers += 1
            }
        }
        setLiveCurrentUserWrongAnswersData()
    }

    fun getWonGame(opponentAnswers: MutableList<Boolean>){
        for (answer in opponentAnswers){
            if (answer) {
                opponentCorrectAnswers += 1
            }
        }
        if (currentUserCorrectAnswers > opponentCorrectAnswers) {
            wonGame = "You have won"
            setLiveWonGameData()
        } else if (currentUserCorrectAnswers < opponentCorrectAnswers) {
            wonGame = "You have lost"
            setLiveWonGameData()
        } else {
            wonGame = "Draw"
            setLiveWonGameData()
        }
    }

}