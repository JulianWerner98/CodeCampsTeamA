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
        tttRepo.startMatchMaking()
    }

    fun getTicTacToeData(): LiveData<TicTacToe> {

        return tictactoeData
    }

    fun setTicTacToeData(value: TicTacToe) {

        tictactoeData.value = value
    }

    //processes the current move
    fun endTurn(index: Int) {

        val ttt = tictactoeData.value
        var newFields = ttt!!.fields
        var icon = 'x'

        if (tictactoeData.value?.isCircle == true) {

            icon = 'o'
        }

        newFields = newFields.substring(0, index) + icon + newFields.substring(index + 1)

        val won = hasWon(icon, newFields)

        ttt.fields = newFields
        setTicTacToeData(ttt)
        tttRepo.sendTurn(index, icon, won)
        Log.d("TAG", newFields)
    }

    fun surrenderGame() {
        val value = tictactoeData.value
        if(value != null){
            tttRepo.surrender(
                if (!value.isCircle) "o" else "x",
                value.players
            )
        }
    }

    //looks if the user has won
    private fun hasWon(icon: Char, fields: String): Boolean {

        /*
        012
        345
        678*/

        //row
        for (i in 0..8 step 3) {

            if (fields[i] == icon && fields[i+1] == icon && fields[i+2] == icon) return true
        }

        //column
        for (i in 0..2) {

            if (fields[i] == icon && fields[i+3] == icon && fields[i+6] == icon) return true
        }

        //vertical
        if (fields[0] == icon && fields[4] == icon && fields[8] == icon) return true

        if (fields[2] == icon && fields[4] == icon && fields[6] == icon) return true

        return false
    }
}