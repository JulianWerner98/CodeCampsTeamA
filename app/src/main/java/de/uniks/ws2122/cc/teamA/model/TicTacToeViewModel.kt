package de.uniks.ws2122.cc.teamA.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.uniks.ws2122.cc.teamA.repository.TicTacToeRepository

class TicTacToeViewModel : ViewModel() {

    private var tttRepo: TicTacToeRepository = TicTacToeRepository()
    private val tictactoeData: MutableLiveData<TicTacToe> = tttRepo.getTicTacToeData()

    init {

        tttRepo.joinQueue()
    }

    fun getTicTacToeData(): LiveData<TicTacToe> {

        return tictactoeData
    }

    fun setTicTacToeData(value: TicTacToe) {

        tictactoeData.value = value
    }

    fun endTurn(index: Int) {

        var ttt = tictactoeData.value
        var newFields = ttt!!.fields
        var icon: Char = 'x'

        if (tictactoeData.value?.isCircle == true) {

            icon = 'o'
        }

        newFields = newFields.substring(0, index) + icon + newFields.substring(index + 1)

        ttt.fields = newFields
        setTicTacToeData(ttt)
        tttRepo.sendTurn(index, icon)
        Log.d("TAG", newFields)
    }
}