package de.uniks.ws2122.cc.teamA.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.repository.MentalArithmeticRepository

class MentalArithmeticViewModel : ViewModel() {
    private var counter = 0
    private var arithmeticTasks = mutableListOf<String>()
    private var arithmeticAnswers = mutableListOf<String>()
    private var arithmeticTasksData = MutableLiveData<List<String>>()
    private var arithmeticAnswersData = MutableLiveData<List<String>>()
    private var mentalArithmeticRepo = MentalArithmeticRepository()
    private var currentUserAnswers = mutableListOf<Boolean>()
    private var currentUserAnswersData = MutableLiveData<List<Boolean>>()

    init {
        arithmeticTasksData.value = arithmeticTasks
        arithmeticAnswersData.value = arithmeticAnswers
        currentUserAnswersData.value = currentUserAnswers
        makeArithmeticTasks()
        mentalArithmeticRepo.lookForGame(arithmeticTasks, arithmeticAnswers)
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
        for (i in 0..9){
            println("$i: " + arithmeticTasksData.value!![i])
            println(arithmeticAnswersData.value!![i])
        }
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



}