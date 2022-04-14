package de.uniks.ws2122.cc.teamA.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.Constant.DRAW
import de.uniks.ws2122.cc.teamA.Constant.FIREBASE_URL
import de.uniks.ws2122.cc.teamA.Constant.GAME
import de.uniks.ws2122.cc.teamA.Constant.GAMES
import de.uniks.ws2122.cc.teamA.Constant.HISTORIE
import de.uniks.ws2122.cc.teamA.Constant.LOSE
import de.uniks.ws2122.cc.teamA.Constant.MATCH_REQUEST
import de.uniks.ws2122.cc.teamA.Constant.NICKNAME
import de.uniks.ws2122.cc.teamA.Constant.STATISTIC
import de.uniks.ws2122.cc.teamA.Constant.TTT
import de.uniks.ws2122.cc.teamA.Constant.USERS_PATH
import de.uniks.ws2122.cc.teamA.Constant.WIN
import de.uniks.ws2122.cc.teamA.model.Highscore
import de.uniks.ws2122.cc.teamA.model.MatchResult
import de.uniks.ws2122.cc.teamA.model.Notification
import de.uniks.ws2122.cc.teamA.model.ticTacToe.TicTacToe
import java.util.*

class TicTacToeRepository {

    //References
    private val rootRef: DatabaseReference =
        FirebaseDatabase.getInstance(FIREBASE_URL).reference
    private var gamesRef: DatabaseReference = rootRef.child(GAMES).ref
    private var tttRef: DatabaseReference = gamesRef.child(TTT).ref
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private val currentUserRef = rootRef.child(USERS_PATH).child(currentUser.uid).ref


    /** Get game from Database **/
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
    /** Get Username by Id**/
    fun getUsername(uid: String, callback: (String) -> Unit) {
        rootRef.child(USERS_PATH).child(uid).child(NICKNAME).get()
            .addOnSuccessListener {
                callback.invoke(it.value.toString())
            }
    }

    /** Create Private Game **/
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

    /** Create game with request **/
    fun createGame(callback: (TicTacToe) -> Unit) {
        createPrivateGame() { ttt ->
            gamesRef.child(MATCH_REQUEST).child(TTT).child(GAME).setValue(ttt.id)
                .addOnSuccessListener {
                    callback.invoke(ttt)
                }
        }
    }
    /** Set listener to current game to detect changes **/
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

    /** Join a game with als dependencies **/
    fun joinGame(callback: (TicTacToe?) -> Unit) {
        gamesRef.child(MATCH_REQUEST).child(TTT).get().addOnSuccessListener { gameIdSnapshot ->
            if (gameIdSnapshot.value != null) {
                val gameId = (gameIdSnapshot.value as HashMap<String, String>)["game"].toString()
                gamesRef.child(MATCH_REQUEST).child(TTT).removeValue().addOnSuccessListener {
                    currentUserRef.child(TTT).setValue(gameId).addOnSuccessListener {
                        getGame() { ttt ->
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

    /** Leave the game with statistic and history and calculate points **/
    fun exitGame(game: TicTacToe?) {
        var userId = currentUser.uid
        rootRef.child(USERS_PATH).child(userId).child(TTT).removeValue()
        var matchResult = MatchResult()
        var opponentId = ""
        opponentId = if (game!!.players[0] == userId) game!!.players[1] else game!!.players[0]
        getUsername(opponentId) { opponentName ->
            matchResult.currentuser = "You"
            matchResult.gamename = TTT
            matchResult.opponent = opponentName
            when (game!!.winner) {
                userId -> {
                    matchResult.points = 3
                    matchResult.win = WIN
                }
                DRAW -> {
                    matchResult.points = 1
                    matchResult.win = DRAW
                }
                else -> {
                    matchResult.points = 0
                    matchResult.win = LOSE
                }
            }
            rootRef.child(USERS_PATH).child(userId).child(Constant.STATISTIC)
                .child(TTT).get().addOnSuccessListener { dataSnapshot ->
                    var highscore =
                        dataSnapshot.getValue(de.uniks.ws2122.cc.teamA.model.Highscore::class.java)
                    if (highscore == null) highscore = Highscore()
                    if (highscore.points < matchResult.points) highscore.points =
                        matchResult.points
                    if (matchResult.win == WIN) highscore.wins += 1
                    if (matchResult.win == LOSE) highscore.loses += 1
                    if (matchResult.win == DRAW) highscore.draws += 1
                    rootRef.child(USERS_PATH).child(userId).child(STATISTIC)
                        .child(TTT).setValue(highscore)
                }
            rootRef.child(USERS_PATH).child(userId).child(STATISTIC)
                .child(HISTORIE).child(game!!.id.toString()).setValue(matchResult)
                .addOnSuccessListener {
                    rootRef.child(USERS_PATH).child(opponentId).child(STATISTIC).child(HISTORIE)
                        .child(game!!.id.toString()).get().addOnSuccessListener {
                            if (it.value != null) {
                                tttRef.child(game!!.id.toString()).removeValue()
                            }
                        }
                }
        }
    }

    /** Delete game **/
    fun deleteGame(game: TicTacToe, callback: () -> Unit) {
        rootRef.child(USERS_PATH).child(currentUser.uid).child(TTT).removeValue()
            .addOnSuccessListener {
                tttRef.child(game.id!!).removeValue().addOnSuccessListener {
                    callback.invoke()
                }
            }
    }

    /** Delete friend invite **/
    fun deleteInvite(friendId: String, callback: () -> Unit) {
        getUsername(currentUser.uid) { ownUsername ->
            rootRef.child(USERS_PATH).child(friendId).child(Constant.INVITES)
                .child(TTT).child(ownUsername)
                .removeValue().addOnSuccessListener {
                    callback.invoke()
                }
        }
    }

    /** Delete Match request query **/
    fun deleteRequest(callback: () -> Unit) {
        gamesRef.child(MATCH_REQUEST).child(TTT).removeValue().addOnSuccessListener {
            callback.invoke()
        }
    }

    /** Join an private game **/
    fun joinPrivateGame(inviteId: String, callback: (TicTacToe?) -> Unit) {
        rootRef.child(USERS_PATH).child(currentUser.uid).child(TTT).setValue(inviteId)
            .addOnSuccessListener {
                getGame { game ->
                    game!!.players.add(currentUser.uid)
                    game!!.turn = currentUser.uid
                    updateGame(game!!)
                    callback.invoke(game)
                }
            }
    }

    /** Send a game Invite a friend **/
    fun sendInvite(gameId: String, friendId: String) {
        getUsername(currentUser.uid) {
            rootRef.child(USERS_PATH).child(friendId).child(Constant.INVITES).child(Constant.TTT)
                .child(it)
                .setValue(gameId)
            val notficationId = gameId
            val notification = Notification(notficationId, it, TTT)
            rootRef.child(Constant.NOTIFICATION).child(Constant.NOTIFICATIONGAMEINVITE)
                .child(friendId).child(notficationId.toString()).setValue(notification)
        }
    }
}

