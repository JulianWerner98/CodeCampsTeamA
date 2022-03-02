package de.uniks.ws2122.cc.teamA.model

import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.repository.MentalArithmeticRepository

class MentalArithmeticViewModel : ViewModel() {
    private var counter = 0
    private var arithmeticTasks = mutableListOf<String>()
    private var arithmeticAnswers = mutableListOf<String>()
    private var currentUserAnswers = mutableListOf<Boolean>()
    private var gameKey = String()
    private var running = false
    private lateinit var chronometer : Chronometer
    private var pauseOffset : Long = 0
    private var friendId = String()
    private var matchTyp = String()
    private var inventionKey = String()

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

    // Logic
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

    fun makeGame() {
        mentalArithmeticRepo.fetchInventionKey() { inventionKey ->
            Log.d("MentalArithmetic", "friendID:  $inventionKey")
            this.inventionKey = inventionKey
            if (inventionKey == "default"){
                mentalArithmeticRepo.lookForGame(arithmeticTasks, arithmeticAnswers, matchTyp, inventionKey, friendId) { key ->
                    gameKey = key
                    fetchArithmeticTasks()
                    fetchArithmeticAnswers()
                }
            } else {
                mentalArithmeticRepo.lookForGame(arithmeticTasks, arithmeticAnswers, matchTyp, inventionKey, friendId) { key ->
                    gameKey = key
                    fetchArithmeticTasks()
                    fetchArithmeticAnswers()
                }
            }
        }
    }

    // Ready up to start the game
    fun readyUpToStartGame(){
        mentalArithmeticRepo.readyUpToStartGame(gameKey) { answer ->
            if (answer){
                chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                chronometer.start()
                //currentUserAnswers.add(false)
                setLiveCurrentUserAnswersData()
                //currentUserAnswers.removeAt(0)
            }
        }
    }

    fun getCurrentTask(): String {
        if (running) {
            if (counter == 3){
                return Constant.WAITINGFOROPPONENT
            }
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

    fun chronometer(chronometer: Chronometer, pauseOffset: Long) {
        this.chronometer = chronometer
        this.pauseOffset = pauseOffset
    }

    fun goToResultActivity(callback: (result: Boolean) -> Unit){
        val time = chronometer.text
        mentalArithmeticRepo.goToResultActivity(gameKey, currentUserAnswers, time as String){ answer ->
            callback.invoke(answer)
        }
    }

    fun setMatchTyp(friendId: String, matchTyp: String, inventionKey: String) {
        this.friendId = friendId
        this.matchTyp = matchTyp
        this.inventionKey = inventionKey
        Log.d("MentalArithmetic", "friendID:  ${this.friendId}")
        Log.d("MentalArithmetic", "friendID:  ${this.matchTyp}")
        Log.d("MentalArithmetic", "friendID:  ${this.inventionKey}")
    }
}