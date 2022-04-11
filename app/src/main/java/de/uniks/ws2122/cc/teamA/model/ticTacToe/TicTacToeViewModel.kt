package de.uniks.ws2122.cc.teamA.model.ticTacToe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.repository.TicTacToeRepository

class TicTacToeViewModel : ViewModel() {

    private var tttRepo: TicTacToeRepository = TicTacToeRepository()
    private var tictactoeData: MutableLiveData<TicTacToe> = MutableLiveData()

    fun getOrCreateGame(friendID: String?) {
        tttRepo.getGame() { ttt ->
            if (ttt != null) {
                tictactoeData.value = ttt
                setListenerToGame()
            } else {
                if (friendID != null) {
                    tttRepo.createPrivateGame() { newTTT ->
                        tictactoeData.value = newTTT
                        setListenerToGame()
                    }
                } else {
                    tttRepo.joinGame() { game ->
                        if(game == null) {
                            tttRepo.createGame() { newTTT ->
                                tictactoeData.value = newTTT
                                setListenerToGame()
                            }
                        } else {
                            tictactoeData.value = game
                            setListenerToGame()
                        }

                    }

                }
            }
        }
    }

    fun setListenerToGame() {
        tttRepo.setListenerToGame(tictactoeData.value!!.id) {
            if (it != null) {
                tictactoeData.value = it
            }
        }
    }

    fun getTicTacToeData(): LiveData<TicTacToe> {
        return tictactoeData
    }

    fun setTicTacToeData(value: TicTacToe) {
        tttRepo.updateGame(value)
    }

    fun surrenderGame() {

    }

    fun turn(index: Int) {
        val game = tictactoeData.value!!
        val symbol = if (isCircle()) "o" else "x"
        if (game.fields[index].isBlank()) {
            game.fields[index] = symbol
            game.winner =
                if (hasWon(symbol, game.fields))
                    FirebaseAuth.getInstance().currentUser!!.uid
                else ""
            if(game!!.winner.isBlank() && checkDraw()) game.winner = "draw"
            game.turn = if(isCircle()) game.players[1] else game.players[0]
            setTicTacToeData(game)
        }
    }

    private fun checkDraw(): Boolean {
        for (i in 0..8) {
            if (tictactoeData.value!!.fields[i].isBlank()) return false
        }
        return true
    }

    //check if the user has won
    private fun hasWon(icon: String, fields: ArrayList<String>): Boolean {

        /*
         012
         345
         678*/

        //row
        for (i in 0..8 step 3) {

            if (fields[i] == icon && fields[i + 1] == icon && fields[i + 2] == icon) return true
        }

        //column
        for (i in 0..2) {

            if (fields[i] == icon && fields[i + 3] == icon && fields[i + 6] == icon) return true
        }

        //vertical
        if (fields[0] == icon && fields[4] == icon && fields[8] == icon) return true

        if (fields[2] == icon && fields[4] == icon && fields[6] == icon) return true
        return false
    }

    fun getNameById(appViewModel: AppViewModel, callback: (String) -> Unit) {
        if (tictactoeData.value!!.players[0] == appViewModel.getUID()) {
            tttRepo.getUsername(tictactoeData.value!!.players[1]) {
                callback.invoke(it)
            }
        } else {
            tttRepo.getUsername(tictactoeData.value!!.players[1]) {
                callback.invoke(it)
            }
        }
    }

    fun isCircle(): Boolean {
        return tictactoeData.value!!.players[0] == FirebaseAuth.getInstance().currentUser!!.uid
    }

}