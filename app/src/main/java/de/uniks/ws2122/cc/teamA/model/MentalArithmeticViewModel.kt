package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.repository.MentalArithmeticRepository

class MentalArithmeticViewModel : ViewModel() {
    private var counter = 0
    private var arithmeticTasks = mutableListOf<String>()
    private var arithmeticAnswers = mutableListOf<String>()
    private var currentUserAnswers = mutableListOf<Boolean>()
    private var gameKey = String()
    private var running = false

    // Live Data
    private var arithmeticTasksData = MutableLiveData<List<String>>()
    private var arithmeticAnswersData = MutableLiveData<List<String>>()
    private var currentUserAnswersData = MutableLiveData<List<Boolean>>()

    // Repo
    private var mentalArithmeticRepo = MentalArithmeticRepository()

    init {
        arithmeticTasksData.value = arithmeticTasks
        arithmeticAnswersData.value = arithmeticAnswers
        currentUserAnswersData.value = currentUserAnswers

        makeArithmeticTasks()
        mentalArithmeticRepo.lookForGame(arithmeticTasks, arithmeticAnswers) { key ->
            gameKey = key
            fetchArithmeticTasks()
            fetchArithmeticAnswers()
        }
    }

    // Setter
    fun setLiveArithmeticTasksData(){
        arithmeticTasksData.value = arithmeticTasks
    }

    fun setLiveArithmeticAnswersData() {
        arithmeticAnswersData.value = arithmeticAnswers
    }

    fun setLiveCurrentUserAnswersData(){
        currentUserAnswersData.value = currentUserAnswers
    }

    // Getter
    fun getLiveArithmeticTasksData(): MutableLiveData<List<String>> {
        return arithmeticTasksData
    }

    fun getLiveArithmeticAnswersData(): MutableLiveData<List<String>> {
        return arithmeticAnswersData
    }

    fun getLiveCurrentUserAnswersData(): MutableLiveData<List<Boolean>> {
        return currentUserAnswersData
    }

    // Create a number of tasks
    fun makeArithmeticTasks(){
        for (i in 0..9){
            when ((0..3).random()) {
                0 -> makeAdditionTask()
                1 -> makeSubtractionTask()
                2 -> makeMultiplicationTask()
                3 -> makeDivisionTask()
            }
        }
        setLiveArithmeticTasksData()
        setLiveArithmeticAnswersData()
    }

    private fun makeAdditionTask() {
        val n1 = (1..20).random()
        val n2 = (1..20).random()
        val answer = n1 + n2
        arithmeticTasks.add("$n1 + $n2")
        arithmeticAnswers.add(answer.toString())
    }

    private fun makeSubtractionTask() {
        val n1 = (1..20).random()
        val n2 = (1..20).random()
        if (n1 >= n2){
            val answer = n1 - n2
            arithmeticTasks.add("$n1 - $n2")
            arithmeticAnswers.add(answer.toString())
        } else {
            val answer = n2 - n1
            arithmeticTasks.add("$n2 - $n1")
            arithmeticAnswers.add(answer.toString())
        }
    }

    private fun makeMultiplicationTask() {
        val n1 = (1..20).random()
        val n2 = (1..20).random()
        val answer = n1 * n2
        arithmeticTasks.add("$n1 * $n2")
        arithmeticAnswers.add(answer.toString())
    }

    private fun makeDivisionTask() {
        val n1 = (1..20).random()
        val n2 = (1..20).random()
        if (n1 >= n2){
            val answer = n1 / n2
            arithmeticTasks.add("$n1 / $n2")
            arithmeticAnswers.add(answer.toString())
        } else {
            val answer = n2 / n1
            arithmeticTasks.add("$n2 / $n1")
            arithmeticAnswers.add(answer.toString())
        }
    }

    // Ready up to start the game
    fun readyUpToStartGame(){
        mentalArithmeticRepo.readyUpToStartGame(gameKey) { answer ->
            if (answer){
                //currentUserAnswers.add(false)
                setLiveCurrentUserAnswersData()
                //currentUserAnswers.removeAt(0)
            }
        }
    }

    fun getCurrentTask(): String {
        if (running) {
            return getLiveArithmeticTasksData().value!![counter]
        } else {
            running = true
            return "Arithmetic Tasks"
        }
    }

    fun fetchArithmeticTasks(){
        mentalArithmeticRepo.fetchArithmeticTasks(gameKey) { tasks ->
            arithmeticTasks = tasks
            setLiveArithmeticTasksData()
        }
    }

    fun fetchArithmeticAnswers(){
        mentalArithmeticRepo.fetchArithmeticAnswers(gameKey) { answers ->
            arithmeticAnswers = answers
            setLiveArithmeticAnswersData()
        }
    }

    // Write true or false in database from current player
    fun sendTaskAnswer(taskAnswer: String) {
        if (taskAnswer == arithmeticAnswers[counter]){
            mentalArithmeticRepo.sendTaskAnswer(true, gameKey, counter.toString())
            counter += 1
            currentUserAnswers.add(true)
            setLiveCurrentUserAnswersData()
        } else {
            mentalArithmeticRepo.sendTaskAnswer(false, gameKey, counter.toString())
            counter += 1
            currentUserAnswers.add(false)
            setLiveCurrentUserAnswersData()
        }
    }
}