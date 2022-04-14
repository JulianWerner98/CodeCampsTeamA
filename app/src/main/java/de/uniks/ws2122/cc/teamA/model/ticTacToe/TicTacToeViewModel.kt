package de.uniks.ws2122.cc.teamA.model.ticTacToe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import de.uniks.ws2122.cc.teamA.Constant.DRAW
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.repository.TicTacToeRepository

class TicTacToeViewModel : ViewModel() {

    private var tttRepo: TicTacToeRepository = TicTacToeRepository()
    private var tictactoeData: MutableLiveData<TicTacToe> = MutableLiveData()

    /** checks if the player is already in game, if not creates a new one **/
    fun getOrCreateGame(friendID: String?, inviteId: String?, callback: (String) -> Unit) {
        // Try to get game
        tttRepo.getGame() { ttt ->
            //There is a game
            if (ttt != null) {
                when {
                    // Want to have a private game
                    friendID != null -> {
                        if(ttt.players.size > 1) {
                            callback.invoke("No Invite! You are already in a game")
                        } else {
                            // Make public game to private and send request to friend
                            tttRepo.deleteRequest {
                                tttRepo.sendInvite(ttt.id!!, friendID)
                            }
                        }
                        tictactoeData.value = ttt
                        setListenerToGame()
                    }
                    //Want to join a private game
                    inviteId != null -> {
                        tttRepo.deleteRequest {
                            tttRepo.deleteGame(ttt){
                                tttRepo.joinPrivateGame(inviteId) {
                                    tictactoeData.value = it
                                    setListenerToGame()
                                }
                            }
                        }
                    }
                    else -> {
                        tictactoeData.value = ttt
                        setListenerToGame()
                    }
                }

            } else {
                //There is no current game
                //Create or join a game
                when {
                    inviteId != null -> {
                        tttRepo.joinPrivateGame(inviteId) {
                            tictactoeData.value = it
                            setListenerToGame()
                        }
                    }
                    friendID != null -> {
                        tttRepo.createPrivateGame() { newTTT ->
                            tttRepo.sendInvite(newTTT.id!!, friendID)
                            tictactoeData.value = newTTT
                            setListenerToGame()
                        }
                    }
                    else -> {
                        tttRepo.joinGame() { game ->
                            if (game == null) {
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
    }

    /** Listen to changes at the game **/
    private fun setListenerToGame() {
        tttRepo.setListenerToGame(tictactoeData.value!!.id) {
            if (it != null) {
                tictactoeData.value = it
            }
        }
    }

    /** Getter **/
    fun getTicTacToeData(): LiveData<TicTacToe> {
        return tictactoeData
    }

    /** Setter **/
    fun setTicTacToeData(value: TicTacToe) {
        tttRepo.updateGame(value)
    }

    /** Surrender Game **/
    fun surrenderGame(appViewModel: AppViewModel) {
        var currentGame = tictactoeData.value
        if (appViewModel.getUID() == currentGame!!.players[0]) {
            currentGame!!.winner = currentGame!!.players[1]
        } else {
            currentGame!!.winner = currentGame!!.players[0]
        }
        setTicTacToeData(currentGame)
    }

    /** Make a possible turn **/
    fun turn(index: Int) {
        val game = tictactoeData.value!!
        val symbol = if (isCircle()) "o" else "x"
        if (game.fields[index].isBlank()) {
            game.fields[index] = symbol
            game.winner =
                if (hasWon(symbol, game.fields))
                    FirebaseAuth.getInstance().currentUser!!.uid
                else ""
            if (game!!.winner.isBlank() && checkDraw()) game.winner = DRAW
            game.turn = if (isCircle()) game.players[1] else game.players[0]
            setTicTacToeData(game)
        }
    }

    /** Check if every field has a symbol**/
    private fun checkDraw(): Boolean {
        for (i in 0..8) {
            if (tictactoeData.value!!.fields[i].isBlank()) return false
        }
        return true
    }

    /** check if the user has won **/
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

    /** get username by id **/
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

    /** check if the own symbol is the circle **/
    fun isCircle(): Boolean {
        return tictactoeData.value!!.players[0] == FirebaseAuth.getInstance().currentUser!!.uid
    }

    /** exit game **/
    fun exitGame() {
        tttRepo.exitGame(tictactoeData.value)
    }

    /** delete game **/
    fun deleteGame(callback: () -> Unit) {
        tttRepo.deleteGame(tictactoeData.value!!, callback)
    }

    /** delete friend invite **/
    fun deleteInvite(friendId: String, callback: () -> Unit) {
        tttRepo.deleteInvite(friendId, callback)
    }

}