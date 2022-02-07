package de.uniks.ws2122.cc.teamA.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicTacToeViewModel: ViewModel() {

    private val tictactoeData: MutableLiveData<TicTacToe> = MutableLiveData()

    fun getTicTacToeData(): LiveData<TicTacToe> {

        return tictactoeData
    }

    fun setTicTacToeData(value: TicTacToe) {

        tictactoeData.value = value
    }

    fun endTurn(index: Int) {

        var newFields = tictactoeData.value?.fields
        var icon: Char = 'x'

        if (tictactoeData.value?.isCircle == true) {

            icon = 'o'
        }

        newFields = newFields?.substring(0, index) + icon + newFields?.substring(index+1)

        tictactoeData.value?.fields = newFields



        Log.d("TAG", newFields)
    }
}