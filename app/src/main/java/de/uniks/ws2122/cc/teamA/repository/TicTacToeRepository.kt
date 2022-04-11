package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant.FIREBASE_URL
import de.uniks.ws2122.cc.teamA.Constant.GAME
import de.uniks.ws2122.cc.teamA.Constant.GAMES
import de.uniks.ws2122.cc.teamA.Constant.MATCH_REQUEST
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME
import de.uniks.ws2122.cc.teamA.Constant.TTT
import de.uniks.ws2122.cc.teamA.Constant.USERS_PATH
import de.uniks.ws2122.cc.teamA.model.ticTacToe.TicTacToe
import java.util.HashMap

class TicTacToeRepository {

    //References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(FIREBASE_URL).reference
    private var gamesRef: DatabaseReference = rootRef.child(GAMES).ref
    private var tttRef: DatabaseReference = gamesRef.child(TTT).ref
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val currentUserRef = rootRef.child(USERS_PATH).child(currentUser.uid).ref


    fun getGame(callback: (TicTacToe?) -> Unit) {
        currentUserRef.child(TTT).get().addOnSuccessListener { gameIdSnapshot ->
            if (gameIdSnapshot.value != null) {
                tttRef.child(gameIdSnapshot.value.toString()).get()
                    .addOnSuccessListener { gameSnapshot ->
                        callback.invoke(gameSnapshot.getValue(TicTacToe::class.java))
                    }
            } else {
                callback.invoke(null)
            }
        }
    }

    fun getUsername(uid: String, callback: (String) -> Unit) {
        rootRef.child(USERS_PATH).child(uid).child(NICKNAME).get()
            .addOnSuccessListener {
                callback.invoke(it.value.toString())
            }
    }

    fun createPrivateGame(callback: (TicTacToe) -> Unit) {
        val newGame = TicTacToe()
        newGame.players.add(currentUser.uid)
        val matchRef = tttRef.push()
        newGame.id = matchRef.key
        tttRef.child(newGame.id!!).setValue(newGame).addOnSuccessListener {
            currentUserRef.child(TTT).setValue(newGame.id).addOnSuccessListener {
                callback.invoke(newGame)
            }
        }
    }

    fun createGame(callback: (TicTacToe) -> Unit) {
        createPrivateGame() { ttt ->
            gamesRef.child(MATCH_REQUEST).child(TTT).child(GAME).setValue(ttt.id)
                .addOnSuccessListener {
                    callback.invoke(ttt)
                }
        }
    }

    fun setListenerToGame(id: String?, callback: (TicTacToe?) -> Unit) {
        tttRef.child(id.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback.invoke(snapshot.getValue(TicTacToe::class.java))
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun updateGame(game: TicTacToe) {
        tttRef.child(game.id!!).setValue(game)
    }

    fun joinGame(callback: (TicTacToe?) -> Unit) {
        gamesRef.child(MATCH_REQUEST).child(TTT).get().addOnSuccessListener { gameIdSnapshot ->
            if(gameIdSnapshot.value != null) {
                val gameId = (gameIdSnapshot.value as HashMap<String, String>)["game"].toString()
                gamesRef.child(MATCH_REQUEST).child(TTT).removeValue().addOnSuccessListener {
                    currentUserRef.child(TTT).setValue(gameId).addOnSuccessListener {
                        getGame(){ ttt ->
                            ttt!!.players.add(currentUser.uid)
                            ttt!!.turn = currentUser.uid
                            updateGame(ttt)
                            callback.invoke(ttt)
                        }
                    }
                }
            } else {
                callback.invoke(null)
            }

        }
    }
}

